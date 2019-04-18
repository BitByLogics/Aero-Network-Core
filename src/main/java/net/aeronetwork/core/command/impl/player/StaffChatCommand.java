package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.server.ServerEnvironment;
import net.aeronetwork.core.util.FM;

import java.util.Arrays;

public class StaffChatCommand extends Command {

    public StaffChatCommand() {
        super("staffchat", "Talk to other staff membrs.", "/sc <message>", Arrays.asList("sc"));
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            player.sendMessage(FM.mainFormat("Staff Chat", "Usage: /sc <message>"));
        } else {
            AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("staff_message", "&8(&c" +
                    AeroCore.SERVER_MANAGER.getServerEnvironment().getEnv(ServerEnvironment.EnvType.AERO_ID) + "&8) "
                    + player.getRank().getPrefix() + player.getAccountName() + " &f" + String.join(" ", details.getArgs())));
        }
    }
}
