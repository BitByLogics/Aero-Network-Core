package net.aeronetwork.api.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Defines the base of creating holograms.
 */
@Getter
public abstract class Hologram {

    private Location location;
    private List<String> text;
    private Map<UUID, List<Integer>> playerHologramIds;

    @Setter
    private double spacing;
    @Setter
    private boolean trackPlayers;

    /**
     * Creates a new instance of Hologram with default values.
     *
     * @param location The location to place the hologram at.
     */
    public Hologram(Location location) {
        this.location = location;
        this.text = Lists.newArrayList();
        this.playerHologramIds = Maps.newConcurrentMap();

        this.spacing = 0.25;
        this.trackPlayers = true;
    }

    /**
     * Adds a new line of text to the hologram.
     *
     * @param text The text to add.
     * @return The instance of Hologram.
     */
    public Hologram addText(String text) {
        this.text.add(text);
        return this;
    }

    /**
     * Shows a player the hologram.
     *
     * @param player The player to show the hologram to.
     */
    public void showHologram(Player player) {
        if(!trackPlayers || playerHologramIds.getOrDefault(player.getUniqueId(), null) == null) {
            List<Integer> ids = Lists.newArrayList();
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            AtomicDouble currentSpacing = new AtomicDouble(this.text.size() * spacing);

            text.forEach(text -> {
                EntityArmorStand stand = new EntityArmorStand(world);
                stand.setCustomName(text);
                stand.setCustomNameVisible(true);
                stand.setLocation(
                        location.getX(),
                        location.getY() + currentSpacing.get(),
                        location.getZ(),
                        0f,
                        0f
                );
                stand.setGravity(false);
                stand.setInvisible(true);
                ids.add(stand.getId());

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(stand));

                currentSpacing.addAndGet(-spacing);
            });

            playerHologramIds.put(player.getUniqueId(), ids);
        }
    }

    /**
     * Hides the hologram for a player.
     *
     * @param player The player to hide the hologram for.
     */
    public void hideHologram(Player player) {
        List<Integer> ids = playerHologramIds.getOrDefault(player.getUniqueId(), null);

        if(ids != null) {
            ids.forEach(id ->
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id)));
            playerHologramIds.remove(player.getUniqueId());
        }
    }
}
