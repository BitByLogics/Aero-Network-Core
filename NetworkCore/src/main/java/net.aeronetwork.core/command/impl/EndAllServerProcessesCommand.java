package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class EndAllServerProcessesCommand extends Command {

    public EndAllServerProcessesCommand() {
        super(
                "endallprocesses",
                "Stops all currently running servers.",
                "endallprocesses",
                null
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Stopping all containers.");
        NetworkCore.NETWORK_MANAGER.getActiveContainers().forEach(NetworkCore.NETWORK_MANAGER::stopContainer);
    }
}
