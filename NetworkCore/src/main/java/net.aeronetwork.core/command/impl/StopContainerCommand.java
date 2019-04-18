package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;
import net.aeronetwork.core.docker.container.AeroContainer;

public class StopContainerCommand extends Command {

    public StopContainerCommand() {
        super(
                "stopcontainer",
                "Stops a container by container Id.",
                "stopcontainer <string:container_id>",
                null
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 1) {
            AeroContainer container = NetworkCore.NETWORK_MANAGER.getContainerByDockerId(args[0]);
            if(container != null) {
                sender.sendMessage("Sending request to stop container " + container.getDockerId() + ". " +
                        "You will be notified when it is stopped.");
                NetworkCore.NETWORK_MANAGER.stopContainer(container).thenAccept(completed -> {
                    if(completed) {
                        sender.sendMessage("Successfully stopped container " + args[0] + "!");
                    } else {
                        sender.sendError("Could not stop container " + args[0] + "! Perhaps it is already stopped?");
                    }
                });
            } else {
                sender.sendError("That container doesn't exist!");
            }
        } else {
            sender.sendError("Please specify a container Id.");
        }
    }
}
