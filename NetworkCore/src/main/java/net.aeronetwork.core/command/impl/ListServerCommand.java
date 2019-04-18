package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class ListServerCommand extends Command {

    public ListServerCommand() {
        super("listserver", "Lists all servers.", "listserver", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("AERO ID - BOUND PORT - JOIN STATE - PLAYERS");
        NetworkCore.NETWORK_MANAGER.getActiveServers().forEach(server ->
            sender.sendMessage(server.getAeroId() + " - " + server.getBoundPort() + " - " +
                    server.getJoinState().name() + " - " + server.getPlayers().size())
        );
    }
}
