package net.aeronetwork.core.redis.impl;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.redis.listener.RedisMessageListener;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DiscordListener extends RedisMessageListener {

    public DiscordListener() {
        super("discord");
    }

    @Override
    public void onReceive(String message) {
        String[] args = message.split(" ");
        if(args[0].equalsIgnoreCase("link")) {
            if(args.length >= 4) {
                if(AeroCore.DISCORD_MANAGER.getDiscordCodes().containsValue(args[1])) {
                    AeroCore.DISCORD_MANAGER.linkAccount(AeroCore.PLAYER_MANAGER.getPlayer(getKeyByValue(AeroCore.DISCORD_MANAGER.getDiscordCodes(), args[1])), args[2], args[3]);
                    AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("discord_bot", "link success " + args[2] + " " + AeroCore.PLAYER_MANAGER.getPlayer(UUID.fromString(args[1])).getAccountName()));
                } else {
                    AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("discord_bot", "link failed null null"));
                }
            }
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
