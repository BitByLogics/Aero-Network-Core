package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;
import net.aeronetwork.core.docker.DockerPackage;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ReloadPackageCommand extends Command {

    public ReloadPackageCommand() {
        super(
                "reloadpackage",
                "Reloads a package.",
                "reloadpackage <string:name> <boolean:build_images>",
                null
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 2) {
            try {
                boolean buildImages = Boolean.valueOf(args[1]);
                sender.sendMessage("Invalidating current package data for " + args[0] + ".");
                NetworkCore.NETWORK_MANAGER.getPackageCache().invalidate(args[0]);

                sender.sendMessage("Loading package data.");
                NetworkCore.NETWORK_MANAGER.loadPackage(args[0]);

                if(buildImages) {
                    sender.sendMessage("Retrieving all package data to build images.");
                    List<DockerPackage> packages = NetworkCore.NETWORK_MANAGER.getPackageCache()
                            .asMap()
                            .getOrDefault(args[0], null);

                    if(packages != null) {
                        AtomicInteger imageBuiltCount = new AtomicInteger(0);
                        sender.sendMessage("Building images for packages (" + packages.size() + " total).");
                        packages.forEach(pkg -> NetworkCore.NETWORK_MANAGER.createImage(pkg).thenAccept(id ->
                                sender.sendMessage(imageBuiltCount.incrementAndGet() + "/" +
                                        packages.size() + " Packages Built")));
                    } else {
                        sender.sendWarning("Seems like the package is invalid.");
                    }
                }
            } catch (Exception e) {
                sender.sendError(e.getMessage());
            }
        } else {
            sender.sendError("Please specify the name of the package and whether to build images.");
        }
    }
}
