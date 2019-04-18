package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class InvalidateRedisCommand extends Command {

    public InvalidateRedisCommand() {
        super("invalidateredis", "Invalidates Redis server cache.",
                "invalidateredis", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        NetworkCore.SERVER_MANAGER.unregisterAll();
        sender.sendMessage("Invalidated all servers from Redis!");
    }
}
