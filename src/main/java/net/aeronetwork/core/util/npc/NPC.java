package net.aeronetwork.core.util.npc;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.util.hologram.Hologram;
import net.aeronetwork.core.util.hologram.impl.BlankHologram;
import net.aeronetwork.core.util.skin.property.SkinProperty;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;
import java.util.UUID;

/**
 * Defines the base level of all NPC implementations.
 */
@Getter
public abstract class NPC {

    private EntityPlayer npc;
    private Location location;

    private GameProfile profile;
    private SkinProperty skinProperty;
    private Hologram text;
    private ArmorStand nameStand;
    @Setter
    private boolean showName = false;

    private boolean onFire = false;
    private boolean isCrouching = false;
    private List<UUID> visiblePlayers;

    public NPC(Location location) {
        this.location = location;

        this.skinProperty = null;

        this.text = new BlankHologram(location.clone().subtract(0, 0.5, 0));
        this.text.setTrackPlayers(true);

        this.visiblePlayers = Lists.newArrayList();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        this.profile = new GameProfile(UUID.randomUUID(), "");

        this.npc = new EntityPlayer(
                server,
                world,
                profile,
                new PlayerInteractManager(world)
        );

        this.npc.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        this.npc.getBukkitEntity().setPlayerListName("§6(NPC) §f" + npc.getId());
    }

    public NPC(Location location, String skinTexture, String skinSignature) {
        this(location);
        profile.getProperties().put("textures", new Property("textures", skinTexture, skinSignature));
        this.skinProperty = new SkinProperty("", skinTexture, skinSignature);
    }

    /**
     * Sets the skin of the NPC.
     *
     * @param skinProperty The data for the skin.
     */
    public void setSkinProperty(SkinProperty skinProperty) {
        this.skinProperty = skinProperty;
        profile.getProperties().put("textures",
                new Property("textures", skinProperty.getTexture(), skinProperty.getSignature()));
    }

    /**
     * Adds text above the NPC's head.
     *
     * @param text The text to add.
     */
    public void addText(String text) {
        this.text.addText(text);
    }

    /**
     * Adds text above the NPC's head.
     *
     * @param text The text to add.
     */
    public void addText(String... text) {
        if(text != null)
            this.text.getText().addAll(Lists.newArrayList(text));
    }

    /**
     * Spawns the NPC for a player.
     *
     * @param player The player to spawn the NPC for.
     */
    public void spawnNPC(Player player) {
        if(!this.visiblePlayers.contains(player.getUniqueId())) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            if(!showName) {
                if(nameStand == null || !nameStand.isValid()) {
                    nameStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    nameStand.setVisible(false);
                    nameStand.setGravity(false);
                }
                npc.getBukkitEntity().setPassenger(nameStand);
            }

            // Spawning
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));

            // Entity Metadata
            DataWatcher dw = npc.getDataWatcher();
            dw.watch(10, (byte) 0xFF);

            byte index0 = (byte) 0x00;

            if(isCrouching)
                index0 = (byte) (index0 + 0x02);
            if(onFire)
                index0 = (byte) (index0 + 0x01);

            if(index0 != 0x00)
                dw.watch(0, index0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dw, false));
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((npc.yaw * 256) / 360)));
                    connection.sendPacket(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc)
                    );
                }
            }.runTaskLater(AeroCore.INSTANCE, 10);

            // Clears scoreboard
            this.npc.getBukkitEntity().getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);

            if(this.text != null)
                this.text.showHologram(player);

            this.visiblePlayers.add(player.getUniqueId());
        }
    }

    /**
     * Despawns the NPC for a player.
     *
     * @param player The player to despawn the NPC for.
     */
    public void despawnNPC(Player player) {
        if(this.visiblePlayers.contains(player.getUniqueId())) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));

            this.text.hideHologram(player);
            this.visiblePlayers.remove(player.getUniqueId());
        }
    }
}
