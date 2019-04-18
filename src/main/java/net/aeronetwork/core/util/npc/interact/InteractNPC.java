package net.aeronetwork.core.util.npc.interact;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.event.packet.in.PacketInEntityUseEvent;
import net.aeronetwork.core.util.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * This implementation of {@link NPC} supports all physical
 * interaction with the NPC.
 */
public abstract class InteractNPC extends NPC implements Listener {

    public InteractNPC(Location location) {
        super(location);
        Bukkit.getServer().getPluginManager().registerEvents(this, AeroCore.INSTANCE);
    }

    public InteractNPC(Location location, String skinTexture, String skinSignature) {
        super(location, skinTexture, skinSignature);
        Bukkit.getServer().getPluginManager().registerEvents(this, AeroCore.INSTANCE);
    }

    public abstract void onInteract(Player player, PacketInEntityUseEvent.InteractAction action);

    @EventHandler
    public final void onEntityUse(PacketInEntityUseEvent e) {
        if(e.getEntityId() == getNpc().getId())
            onInteract(e.getPlayer(), e.getInteractAction());
    }
}
