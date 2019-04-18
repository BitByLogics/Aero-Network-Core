package net.aeronetwork.proxy.redis.listener;

import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.util.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class DisconnectListener extends RedisMessageListener {

    public static String CHANNEL = "disconnect";

    public DisconnectListener() {
        super(CHANNEL);
    }

    // Format: <uuid> [message]
    @Override
    public void onReceive(String message) {
        String[] args = message.split(" ");
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(args[0]));

        if(args.length > 1) {
            player.disconnect(TextComponent.fromLegacyText(Util.join(1, args)));
        } else {
            player.disconnect();
        }
    }
}
