package net.aeronetwork.core.util.npc;

import com.google.common.collect.Lists;
import net.aeronetwork.core.AeroCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.NumberConversions;

import java.util.List;

/**
 * Manages all global NPCs.
 */
public class NPCManager implements Listener {

    private List<NPC> npcs;

    private final int VISIBLE_RANGE = 24;

    public NPCManager() {
        this.npcs = Lists.newArrayList();
        AeroCore.INSTANCE.getServer().getPluginManager().registerEvents(this, AeroCore.INSTANCE);
    }

    /**
     * Registers a new global NPC to be tracked.
     *
     * @param npc The NPC to register.
     */
    public void registerGlobalNPC(NPC npc) {
        this.npcs.add(npc);
        Bukkit.getServer().getOnlinePlayers().forEach(npc::spawnNPC);
    }

    /**
     * Unregisters a global NPC.
     *
     * @param npc The NPC to unregister.
     */
    public void unregisterGlobalNPC(NPC npc) {
        this.npcs.remove(npc);
        Bukkit.getServer().getOnlinePlayers().forEach(npc::despawnNPC);
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        npcs.forEach(npc -> npc.spawnNPC(p));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        npcs.forEach(npc -> {
            if(npc.getLocation().getWorld() == p.getWorld() &&
                    npc.getLocation().distanceSquared(p.getLocation()) <= NumberConversions.square(VISIBLE_RANGE)) {
                npc.spawnNPC(p);
            } else {
                npc.despawnNPC(p);
            }
        });
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        npcs.forEach(npc -> {
            npc.getVisiblePlayers().remove(p.getUniqueId());
            npc.getText().getPlayerHologramIds().remove(p.getUniqueId());
        });
    }
}
