package net.aeronetwork.core.cosmetic;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

/**
 * Represents a class that manages ticking of cosmetics that implement
 * {@link RunnableCosmetic}.
 */
@Getter
public class TickData extends BukkitRunnable {

    private RunnableCosmetic cosmetic;
    private List<UUID> players;

    public TickData(JavaPlugin plugin, RunnableCosmetic cosmetic, long period) {
        this.cosmetic = cosmetic;
        this.players = Lists.newCopyOnWriteArrayList();

        runTaskTimer(plugin, 0, period);
    }

    @Override
    public void run() {
        this.players.forEach(uuid -> {
            Player p = Bukkit.getServer().getPlayer(uuid);
            if(p != null)
                cosmetic.onTick(p);
        });
    }
}
