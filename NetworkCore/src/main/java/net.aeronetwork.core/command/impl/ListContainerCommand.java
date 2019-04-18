package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class ListContainerCommand extends Command {

    public ListContainerCommand() {
        super("listcontainer", "Lists all containers active.", "listcontainer", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("AERO ID - BOUND PORT");
        NetworkCore.NETWORK_MANAGER.getActiveContainers().forEach(container ->
            sender.sendMessage(container.getAeroId() + " - " + container.getBoundPort())
        );
    }

}
