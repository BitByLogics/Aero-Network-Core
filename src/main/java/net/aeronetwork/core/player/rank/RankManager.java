package net.aeronetwork.core.player.rank;

import net.aeronetwork.core.event.player.RankUpdateEvent;
import net.aeronetwork.core.manager.Manager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class RankManager extends Manager {

    public RankManager(JavaPlugin plugin) {
        super("Rank Manager", "Handle rank modification.", plugin);
    }

    /**
     * Add a rank to a player.
     *
     * @param modifier The player modifying the targets ranks (Null if console)
     * @param target The player whose ranks are being modified
     * @param rank The rank being added to the player
     */
    public void addRank(AeroPlayer modifier, AeroPlayer target, Rank rank) {
        if(modifier != null && rank.getPriority() <= modifier.getRank().getPriority() && modifier.getRank() != Rank.OWNER) {
            modifier.sendMessage(FM.mainFormat("Rank", "&cYou cannot add a rank higher or equal to yours."));
            return;
        }

        if(!target.hasRank(rank)) {
            RankUpdateEvent rankUpdateEvent = new RankUpdateEvent(rank, modifier, target, RankUpdateEvent.UpdateType.ADD);
            Bukkit.getServer().getPluginManager().callEvent(rankUpdateEvent);

            if(!rankUpdateEvent.isCancelled()) {
                target.addRank(rank);
                CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "Successfully added &c" + rank.getFriendlyName() + " &eto &c" + target.getAccountName() + "&e's ranks."));
            } else {
                CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "Something stopped the rank from being added..."));
            }
        } else {
            CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "&c" + target.getAccountName() + " &ealready has the &c" + rank.getFriendlyName() + " &erank."));
        }
    }

    /**
     * Remove a rank from a player.
     *
     * @param modifier The player modifying the targets ranks (Null if console)
     * @param target The player whose ranks are being modified
     * @param rank The rank being removed from the player
     */
    public void removeRank(AeroPlayer modifier, AeroPlayer target, Rank rank) {
        if(modifier != null && rank.getPriority() <= modifier.getRank().getPriority() && modifier.getRank() != Rank.OWNER) {
            modifier.sendMessage(FM.mainFormat("Rank", "&cYou cannot remove a rank higher or equal to yours."));
            return;
        }

        if(target.hasRank(rank)) {
            RankUpdateEvent rankUpdateEvent = new RankUpdateEvent(rank, modifier, target, RankUpdateEvent.UpdateType.REMOVE);
            Bukkit.getServer().getPluginManager().callEvent(rankUpdateEvent);

            if(!rankUpdateEvent.isCancelled()) {
                target.revokeRank(rank);
                CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "Successfully removed &c" + rank.getFriendlyName() + " &efrom &c" + target.getAccountName() + "&e's ranks."));
            } else {
                CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "Something stopped the rank from being removed..."));
            }
        } else {
            CommandHelper.replyToSender(modifier, FM.mainFormat("Rank", "&c" + target.getAccountName() + " &edoesn't have the &c" + rank.getFriendlyName() + " &erank."));
        }
    }

    public boolean isValidRank(String rank) {
        return Arrays.stream(Rank.values()).anyMatch(enumRank -> enumRank.name().equalsIgnoreCase(rank.toUpperCase()));
    }

    @EventHandler
    public void onRankUpdate(RankUpdateEvent event) {
        if(event.getUpdateType().equals(RankUpdateEvent.UpdateType.REMOVE)) {
            
        }
    }

}
