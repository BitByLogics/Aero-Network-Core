package net.aeronetwork.core.command.impl.management;

import com.google.common.collect.Lists;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.annotation.SubCommand;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.core.util.FM;

import java.util.Arrays;
import java.util.List;

public class RankCommand extends Command {

    public RankCommand() {
        super("rank", "Modify and view player's ranks.", "/rank", Arrays.asList("setrank", "modifyrank"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Rank Commands: "));
            CommandHelper.replyToSender(player, FM.command("rank list <player>", "View all ranks a player has."));
            CommandHelper.replyToSender(player, FM.command("rank add <player> <rank>", "Add a rank to a player."));
            CommandHelper.replyToSender(player, FM.command("rank remove <player> <rank>", "Remove a rank from a player."));
        }
    }

    @SubCommand(
            name = "list",
            desc = "List all ranks a player has.",
            usage = "/rank list <player>",
            rankRequired = Rank.ADMIN
    )
    public void list(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 1) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

            if(target != null) {
                List<String> ranks = Lists.newArrayList();
                target.getRanks().forEach(rank -> ranks.add(rank.getFriendlyName()));
                CommandHelper.replyToSender(player, FM.mainFormat("Rank", target.getAccountName() + "'s Ranks: "));
                CommandHelper.replyToSender(player, "§c" + String.join("§8, §c", ranks));
            } else {
                CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Invalid player."));
            }
        } else {
            CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Usage: /rank list <player>"));
        }
    }

    @SubCommand(
            name = "add",
            desc = "Add a rank to the player.",
            usage = "/rank add <player> <rank>",
            rankRequired = Rank.ADMIN
    )
    public void add(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 2) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

            if(target != null) {
                if (AeroCore.RANK_MANAGER.isValidRank(details.getArgs()[1])) {
                    AeroCore.RANK_MANAGER.addRank(player, target, Rank.valueOf(details.getArgs()[1].toUpperCase()));
                } else {
                    CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Invalid rank."));
                }
            } else {
                CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Invalid player."));
            }
        } else {
            CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Usage: /rank add <player> <rank...>"));
        }
    }

    @SubCommand(
            name = "remove",
            desc = "Remove a rank from the player.",
            usage = "/rank remove <player> <rank>",
            rankRequired = Rank.ADMIN
    )
    public void remove(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 2) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

            if(target != null) {
                if (AeroCore.RANK_MANAGER.isValidRank(details.getArgs()[1])) {
                    AeroCore.RANK_MANAGER.removeRank(player, target, Rank.valueOf(details.getArgs()[1].toUpperCase()));
                } else {
                    CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Invalid rank."));
                }
            } else {
                CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Invalid player."));
            }
        } else {
            CommandHelper.replyToSender(player, FM.mainFormat("Rank", "Usage: /rank remove <player> <rank...>"));
        }
    }
}
