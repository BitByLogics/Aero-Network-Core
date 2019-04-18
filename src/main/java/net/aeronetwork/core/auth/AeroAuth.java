package net.aeronetwork.core.auth;

import net.aeronetwork.proxy.AeroProxyCore;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Deprecated //TODO: Make a unified REDIS MANAGER
public class AeroAuth {

    public static final String KEY = "aero_player_auth";

    public static AuthResult getAuth(UUID uuid) {
        if(!AeroProxyCore.REDIS_MANAGER.getJedisPool().isClosed()) {
            boolean exists;
            try (Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
                exists = jedis.hexists(KEY, uuid.toString());
            }

            return exists ? AuthResult.AUTHENTICATED : AuthResult.NOT_AUTHENTICATED;
        }
        return AuthResult.NO_CONNECTION;
    }

    public static int getAuthedPlayers() {
        int count = 0;
        if(!AeroProxyCore.REDIS_MANAGER.getJedisPool().isClosed()) {
            try (Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
                count = jedis.hgetAll(KEY).size();
            }
        }
        return count;
    }

    public static AuthResult auth(UUID uuid) {
        if(!AeroProxyCore.REDIS_MANAGER.getJedisPool().isClosed()) {
            AuthResult result;
            try (Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
                Map<String, String> auth = jedis.hgetAll(KEY);
                System.out.println(Arrays.toString(auth.keySet().toArray()));
                if(!jedis.hexists(KEY, uuid.toString())) {
                    jedis.hset(KEY, uuid.toString(), "AUTH");
                    result = AuthResult.AUTHENTICATED;
                } else {
                    result = AuthResult.AUTHENTICATION_EXISTS;
                }
            } catch (Exception e) {
                result = AuthResult.NOT_AUTHENTICATED;
            }
            return result;
        }
        return AuthResult.NO_CONNECTION;
    }

    public static void invalidate(UUID uuid) {
        try (Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
            jedis.hdel(KEY, uuid.toString());
        }
    }

    public static void invalidateAll() {
        try (Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
            Map<String, String> auth = jedis.hgetAll(KEY);
            if(auth != null) {
                if(auth.size() >= 1) jedis.hdel(KEY, auth.keySet().toArray(new String[0]));
            }
        }
    }

    public enum AuthResult {
        AUTHENTICATED,
        AUTHENTICATION_EXISTS,
        NOT_AUTHENTICATED,
        NO_CONNECTION
    }
}
