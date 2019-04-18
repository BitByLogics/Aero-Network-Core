package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class ReloadAllPackagesCommand extends Command {

    public ReloadAllPackagesCommand() {
        super(
                "reloadallpackages",
                "Reloads all packages.",
                "reloadallpackages <boolean:invalidate_current_cache> <boolean:build_images>",
                null
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length >= 2) {
            try {
                boolean invalidate = Boolean.valueOf(args[0]);
                boolean build = Boolean.valueOf(args[1]);
                if(invalidate) {
                    NetworkCore.NETWORK_MANAGER.getPackageCache().invalidateAll();
                    sender.sendMessage("Invalidate the entirety of the package cache. " +
                            "Please note that this could cause games to not meet their quota for some time.");
                    sender.sendWarning("IMPORTANT: If image building is set to false, this " +
                            "will halt all server upscaling.");
                }

                NetworkCore.NETWORK_MANAGER.loadAllPackages();

                if(build) {
                    sender.sendMessage("Beginning image building (this can take some time).");
                    NetworkCore.NETWORK_MANAGER.getPackageCache().asMap().values()
                            .forEach(pkg -> pkg.forEach(NetworkCore.NETWORK_MANAGER::createImage));
                    sender.sendWarning("Processed all package image build. Please note that this can take " +
                            "some time and can cause server quota expectations to not be met.");
                }
            } catch (Exception e) {
                sender.sendError("Could not parse boolean!");
            }
        } else {
            sender.sendError("Please specify whether to invalidate the current cache and build images");
        }
    }
}
