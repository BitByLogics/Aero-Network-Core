package net.aeronetwork.core.redis.impl;

import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.util.Util;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerMessageListener extends RedisMessageListener {

    public static String CHANNEL = "player_message";

    public PlayerMessageListener() {
        super(CHANNEL);
    }

    // Format: <uuid> <message>
    @Override
    public void onReceive(String message) {
        String[] args = message.split(" ");
        if(args.length >= 2) {
            UUID uuid = UUID.fromString(args[0]);
            String sendMessage = Util.join(1, args);

            Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(player -> player.getUniqueId().toString().equals(uuid.toString()))
                    .forEach(player -> player.sendMessage(sendMessage));
        }
    }
}
