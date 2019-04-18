package net.aeronetwork.core.command.impl.player.punish;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.Punishment;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.TimeConverter;
import net.aeronetwork.core.util.Util;

import java.util.Arrays;

public class MuteCommand extends Command {

    public MuteCommand() {
        super("mute", "Mute a player.", "/mute <player> <reason> <length>", null);
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 3) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

            if(target != null) {
                String[] possibleArgs = Util.join(2, details.getArgs()).split(" ");
                StringBuilder reason = new StringBuilder();
                reason.append(details.getArgs()[1] + " ");
                StringBuilder length = new StringBuilder();

                Arrays.stream(possibleArgs).forEach(arg -> {
                    if(TimeConverter.isTimeString(arg)) {
                        length.append(arg + " ");
                    } else if(length.toString().isEmpty()){
                        reason.append(arg + " ");
                    }
                });

                long time = TimeConverter.convert(length.toString().isEmpty() ? "5h" : length.toString());

                AeroCore.PUNISHMENT_MANAGER.punish(target, new Punishment(CommandHelper.getSenderUUID(player), time == -1 ? Punishment.PunishmentType.PERMANENT_MUTE : Punishment.PunishmentType.TEMPORARY_MUTE, reason.toString(), System.currentTimeMillis(), time, true));
            } else {
                CommandHelper.replyToSender(player, FM.mainFormat("Punish", "Invalid player."));
            }
        } else {
            CommandHelper.replyToSender(player, FM.mainFormat("Punish", "Usage: /mute <player> <reason> <length>"));
        }
    }
}
