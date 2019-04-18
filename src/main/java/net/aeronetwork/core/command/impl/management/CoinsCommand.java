package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.annotation.SubCommand;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;

public class CoinsCommand extends Command {

    public CoinsCommand() {
        super(
                "coins",
                "Manage coin related actions.",
                "/coins <add|remove|set> <player> <value>",
                null
        );

        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        CommandHelper.replyToSender(player, "§c" + getUsage());
    }

    @SubCommand(
            name = "add",
            desc = "Add coins to a player.",
            usage = "/coins add <player> <value>",
            rankRequired = Rank.ADMIN,
            minArgs = 2
    )
    public void add(AeroPlayer player, CommandDetails details) {
        AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);
        if(target == null) {
            CommandHelper.replyToSender(player, "§cThat player does not exist!");
            return;
        }
        long value;
        try {
            value = Long.valueOf(details.getArgs()[1]);
        } catch (Exception e) {
            CommandHelper.replyToSender(player, "§cInvalid number!");
            return;
        }

        target.updateCoins(target.getCoins() + value);
    }

    @SubCommand(
            name = "remove",
            desc = "Remove coins from a player.",
            usage = "/coins remove <player> <value>",
            rankRequired = Rank.ADMIN,
            minArgs = 2
    )
    public void remove(AeroPlayer player, CommandDetails details) {
        AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);
        if(target == null) {
            CommandHelper.replyToSender(player, "§cThat player does not exist!");
            return;
        }
        long value;
        try {
            value = Long.valueOf(details.getArgs()[1]);
        } catch (Exception e) {
            CommandHelper.replyToSender(player, "§cInvalid number!");
            return;
        }

        target.updateCoins(target.getCoins() - value);
    }

    @SubCommand(
            name = "set",
            desc = "Set a player's coins.",
            usage = "/coins set <player> <value>",
            rankRequired = Rank.ADMIN,
            minArgs = 2
    )
    public void set(AeroPlayer player, CommandDetails details) {
        AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);
        if(target == null) {
            CommandHelper.replyToSender(player, "§cThat player does not exist!");
            return;
        }
        long value;
        try {
            value = Long.valueOf(details.getArgs()[1]);
        } catch (Exception e) {
            CommandHelper.replyToSender(player, "§cInvalid number!");
            return;
        }

        target.updateCoins(value);
    }
}
