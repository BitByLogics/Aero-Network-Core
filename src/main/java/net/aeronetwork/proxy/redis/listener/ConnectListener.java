package net.aeronetwork.proxy.redis.listener;

import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.server.AeroServer;
import net.aeronetwork.core.util.Util;
import net.aeronetwork.proxy.AeroProxyCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class ConnectListener extends RedisMessageListener {

    public ConnectListener() {
        super("connect");
    }

    @Override
    public void onReceive(String message) {
        // Form: uuid <id|server> <code>
        String[] args = message.split(" ");
        if(args.length >= 3) {
            UUID uuid = UUID.fromString(args[0]);
            ConnectType type = Util.matchEnum(ConnectType.class, args[1]);
            String id = args[2];

            if(type != null) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                if(player != null) {
                    AeroServer server = type == ConnectType.ID ?
                            AeroProxyCore.PROXY_MANAGER.getPriorityServerById(id) :
                            AeroProxyCore.PROXY_MANAGER.getPriorityServerByInstance(id);
                    if(server != null) {
                        AeroProxyCore.PROXY_MANAGER.connectPlayer(player, server, false);
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText("§cCould not find a server with id " +
                                id + "!"));
                    }
                }
            }
        }
    }

    public static String create(UUID uuid, ConnectType type, String id) {
        return uuid.toString() + " " + type.name() + " " + id;
    }

    public enum ConnectType {
        ID,
        SERVER
    }
}
