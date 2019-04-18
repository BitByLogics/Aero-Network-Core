package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "Stop all running processes and the entirety of the network.",
                "stop", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Stopping all network processes.");
        NetworkCore.COMMAND_MANAGER.getCommandService().shutdown();
        NetworkCore.REDIS_MANAGER.getJedisPool().close();
        NetworkCore.NETWORK_MANAGER.getActiveContainers().parallelStream().forEach(container -> {
            sender.sendMessage("Removing container " + container.getAeroId() + " : " + container.getDockerId());
            NetworkCore.NETWORK_MANAGER.getDockerClient().removeContainerCmd(container.getDockerId())
                    .withForce(true)
                    .exec();
        });
        System.exit(0);
    }
}
