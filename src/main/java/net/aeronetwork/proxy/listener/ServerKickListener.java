package net.aeronetwork.proxy.listener;

import net.aeronetwork.core.server.AeroServer;
import net.aeronetwork.proxy.AeroProxyCore;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent e) {
        ServerInfo server = e.getKickedFrom();

        if(!e.getKickedFrom().getName().equalsIgnoreCase("fallback")) {
            AeroServer as = AeroProxyCore.PROXY_MANAGER.getPriorityServerById("arcade_lounge");
            if(as != null) {
                ServerInfo asInfo = AeroProxyCore.PROXY_MANAGER.constructServerInfo(as);
                if(!asInfo.getAddress().getHostName().equals(server.getAddress().getHostName()) ||
                        asInfo.getAddress().getPort() != server.getAddress().getPort()) {
                    e.setCancelServer(asInfo);
                }
            }

            e.getPlayer().sendMessage(TextComponent.fromLegacyText("§cYou were kicked from " +
                    server.getName() + " for: " + BaseComponent.toLegacyText(e.getKickReasonComponent())));

            e.setCancelled(true);
        }
    }
}
