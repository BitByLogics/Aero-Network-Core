package net.aeronetwork.core.command.impl.management;

import com.google.common.base.Joiner;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.CommandHelper;

public class NetworkCommand extends Command {

    public NetworkCommand() {
        super("network", "Manage network related activities.",
                "/network <command>", null);

        setRank(Rank.ADMIN);
        setAllowOp(false);
        setPlayerOnly(false);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 1) {
            AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("network",
                    CommandHelper.getSenderUUID(player) + " " + Joiner.on(" ").join(details.getArgs())));
        } else {
            CommandHelper.replyToSender(player,
                    "§cPlease specify a command or use /network help for more information.");
        }
    }
}
