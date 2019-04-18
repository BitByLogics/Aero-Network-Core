package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.CommandHelper;
import net.aeronetwork.proxy.redis.listener.ConnectListener;

public class JoinCommand extends Command {

    public JoinCommand() {
        super("join", "Connects you to a server.", "/join <id>", null);

        setPlayerOnly(true);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 1) {
            AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("connect",
                    ConnectListener.create(player.getUuid(), ConnectListener.ConnectType.ID, details.getArgs()[0])));
        } else {
            CommandHelper.replyToSender(player, "§cPlease specify the id of the server you want to join.");
            CommandHelper.replyToSender(player, "§aNot sure where you can go? Use §e/info §ato see all the ids!");
        }
    }
}
