package net.aeronetwork.proxy.command;

import net.aeronetwork.proxy.AeroProxyCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "", "lounge", "hub");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            AeroProxyCore.PROXY_MANAGER.connectPlayer((ProxiedPlayer) sender, "arcade_lounge", false);
        }
    }
}
