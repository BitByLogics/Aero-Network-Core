package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class VanishCommand extends Command {

    public VanishCommand() {
        super("vanish", "Vanish.", "/vanish", Arrays.asList("v"));
        setAllowedInGame(false);
        setRank(Rank.YT);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player.isVanished()) {
            player.updateVanished(false);
            player.sendMessage(FM.mainFormat("Vanish", "You're no longer vanished."));
            updatePlayers(player);
        } else {
            player.updateVanished(true);
            player.sendMessage(FM.mainFormat("Vanish", "You're now vanished."));
            updatePlayers(player);
        }
    }

    public void updatePlayers(AeroPlayer player) {
        if(player.isVanished()) {
            for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                if(bukkitPlayer != player.getBukkitPlayer() && bukkitPlayer.canSee(player.getBukkitPlayer())) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(bukkitPlayer.getUniqueId());
                    if(target.getRank().getRankType().equals(player.getRank().getRankType())
                            || target.getRank().getRankType().equals(Rank.RankType.HIGH_STAFF)
                            || target.getRank().getPriority() < player.getRank().getPriority()) {

                    } else {
                        bukkitPlayer.hidePlayer(player.getBukkitPlayer());
                    }
                }
            }
        } else {
            for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                if(bukkitPlayer != player.getBukkitPlayer()) {
                    bukkitPlayer.showPlayer(player.getBukkitPlayer());
                }
            }
        }
    }
}
