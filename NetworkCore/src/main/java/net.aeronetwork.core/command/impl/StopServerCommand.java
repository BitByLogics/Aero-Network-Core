package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;
import net.aeronetwork.core.server.AeroServer;

public class StopServerCommand extends Command {

    public StopServerCommand() {
        super("stopserver", "Stops a server by Aero Id.", "stopserver <aero_id>", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 1) {
            AeroServer server = NetworkCore.NETWORK_MANAGER.getServerByAeroId(args[0]);
            if(server != null) {
                sender.sendMessage("Sending request to stop server " + server.getAeroId() + ". " +
                        "You will be notified when it is stopped.");
                sender.sendWarning("If you do not receive a message within a minute, " +
                        "the server most likely stopped successfully.");
                NetworkCore.NETWORK_MANAGER.stopServer(server).thenAccept(completed -> {
                    if(completed) {
                        sender.sendMessage("Successfully stopped server " + server.getAeroId() + "!");
                    } else {
                        sender.sendError("Could not stop server " + server.getAeroId() + "! " +
                                "Perhaps it is already stopped?");
                    }
                });
            } else {
                sender.sendError("That server doesn't exist!");
            }
        } else {
            sender.sendError("Please specify the Aero Id of the server you wish to stop.");
        }
    }
}
