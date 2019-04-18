package net.aeronetwork.core.redis.command.info;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Defines the load to be sent to commands that are listening
 * for commands with the specified command name.
 */
@Data
@AllArgsConstructor
public class RedisCommandDetails {

    private String executedCommandName;
    private String[] args;

    @Override
    public RedisCommandDetails clone() {
        try {
            return (RedisCommandDetails) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
