package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.annotation.SubCommand;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;

public class CrystalsCommand extends Command {

    public CrystalsCommand() {
        super(
                "crystals",
                "Manage crystal related actions.",
                "/crystals <add|remove|set> <player> <value>",
                null
        );
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        CommandHelper.replyToSender(player, "§c" + getUsage());
    }

    @SubCommand(
            name = "add",
            desc = "Add crystals to a player.",
            usage = "/crystals add <player> <value>",
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

        target.updateCrystals(target.getCoins() + value);
    }

    @SubCommand(
            name = "remove",
            desc = "Remove crystals from a player.",
            usage = "/crystals remove <player> <value>",
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

        target.updateCrystals(target.getCoins() - value);
    }

    @SubCommand(
            name = "set",
            desc = "Set a player's crystals.",
            usage = "/crystals set <player> <value>",
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

        target.updateCrystals(value);
    }
}
