package net.aeronetwork.core.player.disguise.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import com.nametagedit.plugin.NametagEdit;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.player.disguise.DisguiseData;
import net.aeronetwork.core.player.disguise.SkinData;
import net.aeronetwork.core.util.UUIDUtil;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class DisguiseUtils {

    public static HashMap<String, SkinData> skinCache = Maps.newHashMap();

    /**
     * Updates a player with the data in {@link DisguiseData}.
     *
     * @param player The player to update.
     * @param data The disguise data to apply.
     */
    public static void updatePlayer(Player player, DisguiseData data) {
        modifyGameProfile(data.getName(), data.getSkin(), ((CraftPlayer) player).getProfile());
        updateSelf(player);
        updateAll(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                NametagEdit.getApi().clearNametag(player.getName());
                NametagEdit.getApi().setNametag(
                        player.getName(),
                        data.getRank().getPrefix(),
                        data.getRank().getSuffix()
                );
            }
        }.runTaskLater(AeroCore.INSTANCE, 0);
    }
    
    /**
     * Modify the specified GameProfile.
     * 
     * @param name The name being injected
     * @param skin The skin being injected
     * @param profile The GameProfile being modified
     */
    public static void modifyGameProfile(String name, String skin, GameProfile profile) {
        PropertyMap pm = profile.getProperties();

        if (!pm.get("textures").isEmpty()) {
            pm.remove("textures", pm.get("textures").iterator().next());
        }

        try {
            Field field = profile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(profile, name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "[DISGUISE] COULD NOT CHANGE NAME IN GAME PROFILE: " + profile.toString());
            e.printStackTrace();
        }

        setSkin(pm, skin);
    }

    /**
     * Set a player's skin.
     * 
     * @param profile The PropertyMap being modified.
     * @param name The name of the skin we're retrieving
     * @return Whether or not we successfully set the skin
     */
    public static boolean setSkin(PropertyMap profile, String name) {
        if (!skinCache.containsKey(name.toLowerCase())) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(
                        String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false",
                                UUIDTypeAdapter.fromUUID(UUIDUtil.getUUID(name)))).openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    String skin = reply.split("\"value\":\"")[1].split("\"")[0];
                    String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
                    skinCache.put(name.toLowerCase(), new SkinData(skin, signature));
                    profile.put("textures", new Property("textures", skin, signature));
                    return true;
                } else {
                    System.out.println("[Disguise Manager] Connection could not be opened (Response code " + connection.getResponseCode()
                            + ", " + connection.getResponseMessage() + ")");
                    System.out.println("[Disguise Manager] Returning skin value as Steve.");
                    profile.put("textures", new Property("textures", "", ""));
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            SkinData skinData = skinCache.get(name.toLowerCase());
            profile.put("textures", new Property("textures", skinData.getSkinHash(), skinData.getSignature()));
            return false;
        }
    }

    
    /**
     * Update the player's skin.
     * 
     * @param p The player whose skin is being updated.
     */
    public static void updateSelf(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        List<Packet> packets = Lists.newArrayList(
                new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep),
                new PacketPlayOutEntityDestroy(p.getEntityId()),
                new PacketPlayOutNamedEntitySpawn(ep),
                new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep),
                new PacketPlayOutRespawn(
                        retrieveDimension(p),
                        ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(),
                        ep.playerInteractManager.getGameMode()
                ),
                new PacketPlayOutPosition(ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch, new HashSet<>()),
                new PacketPlayOutHeldItemSlot(p.getInventory().getHeldItemSlot())
        );
        packets.forEach(ep.playerConnection::sendPacket);
        p.setFlying(ep.getBukkitEntity().isFlying());
        p.setExp(ep.exp);
        p.updateInventory();
    }

    /**
     * Get the player's dimension (Due to bug with Spigot).
     * 
     * @param player The player whose dimension is being retrieved
     * @return The player's dimension ID
     */
    public static int retrieveDimension(Player player) {
        if (player.getWorld().getEnvironment().equals(Environment.NORMAL)) {
            return 0;
        } else if (player.getWorld().getEnvironment().equals(Environment.NETHER)) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Update a player's skin for all other players.
     * 
     * @param p The player whose skin is being updated
     */
    public static void updateAll(Player p) {
        for (Player pp : Bukkit.getOnlinePlayers()) {
            if (pp.canSee(p)) {
                pp.hidePlayer(p);
                pp.showPlayer(p);
            }
        }
    }

}
