package net.aeronetwork.core.util.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.aeronetwork.core.util.UUIDUtil;
import net.aeronetwork.core.util.skin.property.SkinProperty;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Utility class that fetches and applies player skins. Due to how the Mojang
 * API for retrieving player data works, the data for a single player can only
 * be requested once every few minutes, while retrieving data for unique users
 * can be requested infinite amount of times a minute. As a result, skins will
 * be cached for 10 minutes, which prevents throttling issues, as well as lower
 * the amount of resources required to fetch skins.
 *
 * As there is only one source for fetching skins, if the API server is down,
 * this util is essentially rendered as useless for fetching new skins, but
 * previously cached skins will still be available until they are invalidated.
 * If the API server does happen to be down, this util could cause a chain of
 * exceptions occurring.
 */
public class SkinUtil {

    private static Cache<String, SkinProperty> cachedSkins;

    /**
     * The main URL for fetching skins.
     */
    public static final String GAME_API = "https://sessionserver.mojang.com/session/minecraft/profile/";

    // Prevents initialization
    private SkinUtil() {
    }

    static {
        cachedSkins = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Gets the cached skin information for a name, or fetches
     * new data if the current data has expired or doesn't exist.
     *
     * @param name The name of the player to retrieve skin data for.
     * @return An instance of {@link SkinProperty} containing all data
     * of the specified player's skin.
     */
    public static SkinProperty getSkin(String name) {
        SkinProperty property = cachedSkins.getIfPresent(name.toLowerCase());
        if(property == null) {
            try {
                UUID uuid = UUIDUtil.getUUID(name);
                if(uuid != null) {
                    String cleanUUID = uuid.toString().replaceAll("-", "");

                    HttpResponse<JsonNode> response = Unirest.get(GAME_API + cleanUUID + "?unsigned=false").asJson();

                    JsonNode body = response.getBody();

                    JSONObject main = body.getObject();
                    JSONArray array = main.getJSONArray("properties");
                    JSONObject object = array.getJSONObject(0);
                    String value = object.getString("value");
                    String signature = object.getString("signature");

                    property = new SkinProperty(name, value, signature);
                    cachedSkins.put(name.toLowerCase(), property);
                    return property;
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
        return property;
    }

    /**
     * Changes a player's skin.
     *
     * @param player The player to change the skin for.
     * @param property The {@link SkinProperty} containing all data
     *                 about the skin.
     */
    public static void changePlayerSkin(Player player, SkinProperty property) {
        changePlayerSkin(player, property.getTexture(), property.getSignature());
    }

    /**
     * Changes a player's skin.
     *
     * @param player The player to change the skin for.
     * @param texture The texture of the skin.
     * @param signature The signature of the skin.
     */
    public static void changePlayerSkin(Player player, String texture, String signature) {
        try {
            EntityPlayer ep = ((CraftPlayer) player).getHandle();

            Field profile = ep.getClass().getSuperclass().getDeclaredField("gameProfile");
            profile.setAccessible(true);

            GameProfile gp = new GameProfile(player.getUniqueId(), player.getName());
            gp.getProperties().put("textures", new Property("texture", texture, signature));
            profile.set(ep, gp);

            List<Packet> packets = Lists.newArrayList(
                    new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep),
                    new PacketPlayOutEntityDestroy(player.getEntityId()),
                    new PacketPlayOutNamedEntitySpawn(ep),
                    new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep),
                    new PacketPlayOutRespawn(
                            player.getWorld().getEnvironment().getId(),
                            ep.getWorld().getDifficulty(),
                            WorldType.getType(player.getWorld().getWorldType().getName()),
                            ep.playerInteractManager.getGameMode()
                    ),
                    new PacketPlayOutPosition(
                            ep.locX,
                            ep.locY,
                            ep.locZ,
                            ep.yaw,
                            ep.pitch,
                            Sets.newHashSet()
                    ),
                    new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot())
            );

            packets.forEach(ep.playerConnection::sendPacket);

            player.setFlying(player.isFlying());
            player.setExhaustion(ep.exp);
            player.updateInventory();

            Bukkit.getServer().getOnlinePlayers().forEach(online -> {
                if(online != player) {
                    online.hidePlayer(player);
                    online.showPlayer(player);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
