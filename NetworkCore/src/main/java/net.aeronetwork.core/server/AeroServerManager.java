package net.aeronetwork.core.server;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.aeronetwork.core.NetworkCore;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Manages Redis caching of {@link AeroServer}.
 */
public class AeroServerManager {

    private Gson gson;

    private final String SERVER_KEY = "aero_servers";

    public AeroServerManager() {
        this.gson = new Gson();
    }

    /**
     * Registers a new server as a valid {@link AeroServer} in Redis.
     *
     * @param aeroId The ID of the server.
     * @param instance The instance name of the server.
     * @param ids All related IDs for the server.
     * @param ip The host IP of the server.
     * @param boundPort The port the server is bound to.
     * @param type The type of server deployed.
     */
    public void registerServer(String aeroId, String instance, List<String> ids, String ip, int boundPort, ServerType type) {
        AeroServer server = new AeroServer();
        server.setAeroId(aeroId);
        server.setInstance(instance);
        server.setIds(ids);
        server.setIp(ip);
        server.setBoundPort(boundPort);
        server.setPrivateServer(false);
        server.setServerType(type);

        NetworkCore.NETWORK_MANAGER.getActiveServers().add(server);

        try (Jedis jedis = NetworkCore.REDIS_MANAGER.getJedisPool().getResource()) {
            jedis.hset(SERVER_KEY, aeroId, gson.toJson(server));
        }
    }

    /**
     * Invalidates an {@link AeroServer} from Redis.
     *
     * @param aeroId The Aero ID of the server to invalidate.
     */
    public void unregisterServer(String aeroId) {
        try (Jedis jedis = NetworkCore.REDIS_MANAGER.getJedisPool().getResource()) {
            jedis.hdel(SERVER_KEY, aeroId);
        }
    }

    /**
     * Invalidates all servers from Redis. Incorrect/careless usage of this
     * command could lead to issues runtime.
     */
    public void unregisterAll() {
        List<String> ids = Lists.newArrayList();
        try (Jedis jedis = NetworkCore.REDIS_MANAGER.getJedisPool().getResource()) {
            jedis.hgetAll(SERVER_KEY).forEach((id, server) -> ids.add(id));
            jedis.hdel(SERVER_KEY, ids.toArray(new String[0]));
        }
    }
}
