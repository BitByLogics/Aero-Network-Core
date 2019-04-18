package net.aeronetwork.core.command.impl.player.punish;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.Punishment;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;

public class WarnCommand extends Command {

    public WarnCommand() {
        super("warn", "Warn a player.", "/warn <player> <reason>", null);
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 2) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

            if(target != null) {
                String reason = Util.join(1, details.getArgs());

                AeroCore.PUNISHMENT_MANAGER.punish(target, new Punishment(CommandHelper.getSenderUUID(player), Punishment.PunishmentType.WARNING, reason, System.currentTimeMillis(), -1, true));
            } else {
                CommandHelper.replyToSender(player, FM.mainFormat("Punish", "Invalid player."));
            }
        } else {
            CommandHelper.replyToSender(player, FM.mainFormat("Punish", "Usage: /warn <player> <reason>"));
        }
    }
}
