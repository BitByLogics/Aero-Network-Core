package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;
import net.aeronetwork.core.docker.DockerPackage;
import net.aeronetwork.core.util.Callback;

public class StartServerCommand extends Command {

    public StartServerCommand() {
        super("startserver", "Starts a server by id.", "startserver <id>", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 1) {
            DockerPackage pkg = NetworkCore.NETWORK_MANAGER.getPackage(args[0]);
            if(pkg != null) {
                sender.sendMessage("Starting package...");
                NetworkCore.NETWORK_MANAGER.startPackage(pkg, new Callback<String>() {
                    @Override
                    public void info(String s) {
                        sender.sendMessage(s);
                    }

                    @Override
                    public void error(String s) {
                        sender.sendError(s);
                    }
                });
            } else {
                sender.sendError("§cInvalid package id.");
            }
        } else {
            sender.sendError("§cPlease specify a package id.");
        }
    }
}
