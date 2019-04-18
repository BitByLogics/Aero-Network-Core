package net.aeronetwork.core.util.bossbar;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.AeroCore;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class BossBar extends BukkitRunnable {

    private String text;

    private double maxProgress;
    private float progress;

    @Setter(AccessLevel.NONE)
    private Map<UUID, EntityWither> players;

    private boolean running;

    public BossBar(String text) {
        this.text = text;
        this.maxProgress = 1.0d;
        this.progress = 1.0f;
        this.players = Maps.newHashMap();

        this.running = true;

        this.runTaskTimer(AeroCore.INSTANCE, 0, 10);
    }

    /**
     * Updates the boss bar for all players.
     */
    public void update() {
        players.forEach((uuid, wither) -> {
            Player p = Bukkit.getServer().getPlayer(uuid);
            if(p != null) {
                removeBar(uuid, wither);
                Vector direction = p.getLocation().getDirection().clone();
                Location location = p.getLocation().clone().add(direction.multiply(20));
                location.add(0, 10, 0);
                EntityWither bossBar = new EntityWither(((CraftWorld) p.getWorld()).getHandle());
                bossBar.setCustomName(text);
                bossBar.setCustomNameVisible(true);
                bossBar.setInvisible(true);
                bossBar.setLocation(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getPitch(),
                        location.getYaw()
                );
//                bossBar.getAttributeInstance(GenericAttributes.maxHealth).setValue(maxProgress);
//                bossBar.setHealth(progress);

                PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bossBar);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                players.replace(uuid, bossBar);
            }
        });
    }

    /**
     * Adds a player to the boss bar.
     *
     * @param player The player to show the boss bar to.
     */
    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), null);
    }

    /**
     * Removes a boss bar from a player.
     *
     * @param player The player to remove the boss bar from.
     */
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        removeBar(player.getUniqueId());
    }

    /**
     * Removes a boss bar from a player.
     *
     * @param uuid The UUID of the player to remove the boss bar
     *             from.
     */
    public void removeBar(UUID uuid) {
        Player p = Bukkit.getServer().getPlayer(uuid);
        if(p != null) {
            EntityWither currentBar = players.getOrDefault(uuid, null);
            if(currentBar != null) {
                removeBar(uuid, currentBar);
            }
        }
    }

    /**
     * Removes a boss bar from a player.
     *
     * Internal method.
     *
     * @param uuid The UUID of the player to remove the boss bar
     *             from.
     * @param wither The EntityWither acting as the boss bar.
     */
    private void removeBar(UUID uuid, EntityWither wither) {
        Player p = Bukkit.getServer().getPlayer(uuid);
        if(p != null && wither != null) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    /**
     * Removes boss bars from all players.
     */
    public void removeAllBars() {
        players.forEach(this::removeBar);
    }

    /**
     * Stops all running processes for this boss bar, resulting
     * in this BossBar instance being invalidated.
     */
    public void stop() {
        this.running = false;
        super.cancel();
        removeAllBars();
    }

    /**
     * Manages the updating and cancelling of the boss bar.
     */
    @Override
    public void run() {
        if(running && players.size() >= 1) {
            update();
        } else if(!running) {
            super.cancel();
        }
    }
}
