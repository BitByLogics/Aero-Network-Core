package net.aeronetwork.proxy.listener;

import com.google.common.collect.Lists;
import net.aeronetwork.core.auth.AeroAuth;
import net.aeronetwork.core.network.NetworkManager;
import net.aeronetwork.core.player.network.NetworkPlayer;
import net.aeronetwork.core.server.AeroServer;
import net.aeronetwork.proxy.AeroProxyCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ConnectionListener implements Listener {

    private List<UUID> logins;

    public ConnectionListener() {
        this.logins = Lists.newCopyOnWriteArrayList();
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        AeroAuth.AuthResult result = AeroAuth.auth(uuid);

        String message = null;

        switch (result) {
            case AUTHENTICATED:
                break;
            case AUTHENTICATION_EXISTS:
                message = "Could not authenticate! Another player on your account?";
                break;
            case NOT_AUTHENTICATED:
                message = "Could not authenticate!";
                break;
            case NO_CONNECTION:
                message = "Network is currently down!";
                break;
            default:
                message = "An unknown error occurred while authenticating.";
                break;
        }

        if(result != AeroAuth.AuthResult.AUTHENTICATED) {
            e.setCancelled(true);
            e.setCancelReason(TextComponent.fromLegacyText("§c" + message));
        } else {
            NetworkManager.getInstance().addPlayer(new NetworkPlayer(
                    e.getConnection().getUniqueId(),
                    e.getConnection().getName()
            ));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProxyConnect(ServerConnectEvent e) {
        if(!this.logins.contains(e.getPlayer().getUniqueId())) {
            AeroServer server = AeroProxyCore.PROXY_MANAGER.getPriorityServerById("arcade_lounge");
            if(server != null) {
                this.logins.add(e.getPlayer().getUniqueId());
                ProxyServer.getInstance().getScheduler().schedule(AeroProxyCore.INSTANCE, () -> AeroProxyCore.PROXY_MANAGER.connectPlayer(e.getPlayer(), server, false), 500, TimeUnit.MILLISECONDS);
            } else {
                e.setCancelled(true);
                e.getPlayer().disconnect(TextComponent.fromLegacyText("§cCould not find a server to connect you to!"));
            }
        }

        NetworkManager.getInstance().updatePlayer(new NetworkPlayer(
                e.getPlayer().getUniqueId(),
                e.getPlayer().getName(),
                e.getPlayer().getServer().getInfo().getName()
        ));
    }

    @EventHandler
    public void onProxyDisconnect(PlayerDisconnectEvent e) {
        AeroAuth.invalidate(e.getPlayer().getUniqueId());
        this.logins.remove(e.getPlayer().getUniqueId());

        NetworkManager.getInstance().removePlayer(e.getPlayer().getUniqueId());
    }
}
