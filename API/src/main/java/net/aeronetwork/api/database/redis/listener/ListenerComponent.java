package net.aeronetwork.api.database.redis.listener;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Contains all data to be sent to Redis listener
 * that are listening to the specified channel.
 */
@Data
@AllArgsConstructor
public class ListenerComponent {

    private String channel;
    private String message;
}
