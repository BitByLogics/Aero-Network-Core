package net.aeronetwork.api.database.redis.command.listener;

import net.aeronetwork.api.database.redis.command.info.RedisCommandDetails;
import net.aeronetwork.api.database.redis.listener.RedisMessageListener;

/**
 * Handles all command dispatching.
 */
public class RedisCommandListener extends RedisMessageListener {

    public static final String COMMAND_CHANNEL = "COMMAND_CHANNEL";

    public RedisCommandListener() {
        super(COMMAND_CHANNEL);
    }

    @Override
    public void onReceive(String message) {
        RedisCommandDetails details = StaticAssets.REDIS_MANAGER.getGson().fromJson(message, RedisCommandDetails.class);
        StaticAssets.REDIS_MANAGER.getCommands().stream()
                .filter(command -> command.getName().equalsIgnoreCase(details.getExecutedCommandName()) ||
                        command.getAliases().stream()
                                .filter(alias -> alias.equalsIgnoreCase(details.getExecutedCommandName()))
                                .findFirst()
                                .orElse(null) != null)
                .forEach(command -> command.execute(details.clone()));
    }
}
