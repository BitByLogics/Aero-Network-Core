package net.aeronetwork.core.redis.impl;

import com.google.common.base.Joiner;
import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.CommandSender;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.redis.listener.RedisMessageListener;

import java.util.Arrays;
import java.util.UUID;

public class NetworkMessageListener extends RedisMessageListener {

    public NetworkMessageListener() {
        super("network");
    }

    @Override
    public void onReceive(String message) {
        String[] args = message.split(" ");
        if(args.length >= 2) {
            UUID uuid = UUID.fromString(args[0]);
            String command = (args.length >= 3 ? Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length)) :
                    args[1]);
            CommandSender sender = new CommandSender() {
                @Override
                public void sendMessage(String message) {
                    NetworkCore.REDIS_MANAGER.sendListenerMessage(
                            new ListenerComponent("player_message", uuid.toString() + " §a" + message)
                    );
                }

                @Override
                public void sendWarning(String warning) {
                    NetworkCore.REDIS_MANAGER.sendListenerMessage(
                            new ListenerComponent("player_message", uuid.toString() + " §6" + warning)
                    );
                }

                @Override
                public void sendError(String error) {
                    NetworkCore.REDIS_MANAGER.sendListenerMessage(
                            new ListenerComponent("player_message", uuid.toString() + " §c" + error)
                    );
                }
            };
            boolean executed = NetworkCore.COMMAND_MANAGER.executeCommand(sender, command);

            if(!executed)
                sender.sendError("That command doesn't exist. Use help for more information (/network help).");
        }
    }
}
