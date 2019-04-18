package net.aeronetwork.core.command.impl.random;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;

import java.util.Collections;

public class AnnounceCommand extends Command {

    public AnnounceCommand() {
        super("announce", "Send a message across all servers.", "/announce <message>", Collections.singletonList("ann"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            CommandHelper.replyToSender(player, FM.mainFormat("Usage: /announce <message>"));
        } else {
            AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("announce", FM.translate(Util.join(0, details.getArgs()))));
        }
    }

}
