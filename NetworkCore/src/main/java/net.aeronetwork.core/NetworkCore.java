package net.aeronetwork.core;

import net.aeronetwork.core.command.CommandManager;
import net.aeronetwork.core.docker.NetworkManager;
import net.aeronetwork.core.redis.RedisManager;
import net.aeronetwork.core.server.AeroServerManager;

/**
 * Acts as the bootstrap for the network core.
 */
public class NetworkCore {

    // All static assets
    public static NetworkManager NETWORK_MANAGER = new NetworkManager();
    public static RedisManager REDIS_MANAGER = new RedisManager();
    public static AeroServerManager SERVER_MANAGER = new AeroServerManager();
    public static CommandManager COMMAND_MANAGER = new CommandManager();

    public NetworkCore() {
    }

    public static void main(String[] args) {
        new NetworkCore();
    }
}
