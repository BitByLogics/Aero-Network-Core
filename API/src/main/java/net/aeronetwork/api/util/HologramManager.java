package net.aeronetwork.api.util;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class HologramManager implements Listener {

    private List<Hologram> holograms;

    public HologramManager(JavaPlugin plugin) {
        this.holograms = Lists.newArrayList();

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Registers a new global hologram.
     *
     * @param hologram The hologram to register.
     */
    public void registerGlobalHologram(Hologram hologram) {
        this.holograms.add(hologram);
    }

    public Hologram createHologram(Location location) {
        return new BlankHologram(location);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        holograms.forEach(hologram -> hologram.showHologram(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {holograms.forEach(hologram -> hologram.getPlayerHologramIds().remove(event.getPlayer().getUniqueId()));}
}
