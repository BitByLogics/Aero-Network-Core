package net.aeronetwork.api.database.redis.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines a base listener that will listen on the
 * specified channel.
 */
@AllArgsConstructor
@Getter
public abstract class RedisMessageListener {

    private String channelName;

    /**
     * Called when an incoming Redis message matches
     * the specified channel name.
     *
     * @param message The message associated with the incoming
     *                channel message.
     */
    public abstract void onReceive(String message);
}
