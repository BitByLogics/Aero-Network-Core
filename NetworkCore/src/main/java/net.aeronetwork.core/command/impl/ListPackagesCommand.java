package net.aeronetwork.core.command.impl;

import com.google.common.collect.Lists;
import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ListPackagesCommand extends Command {

    public ListPackagesCommand() {
        super("listpackages", "Lists all available packages.", "listpackages", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("INSTANCE -> [LIST OF FIRST IDs]");
        NetworkCore.NETWORK_MANAGER.getPackageCache().asMap().forEach((instance, pkgs) -> {
            List<String> ids = Lists.newArrayList();
            pkgs.forEach(pkg -> ids.add(pkg.getIds().get(0)));
            sender.sendMessage(instance + " -> " + Arrays.toString(ids.toArray()));
        });
    }
}
