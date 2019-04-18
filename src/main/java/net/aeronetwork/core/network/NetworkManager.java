package net.aeronetwork.core.network;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Getter;
import net.aeronetwork.core.player.network.NetworkPlayer;
import net.aeronetwork.core.redis.RedisManager;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class NetworkManager {

    @Getter
    private static NetworkManager instance = new NetworkManager();

    @Getter
    private RedisManager redisManager;
    private Gson gson;

    private final String NETWORK_PLAYERS_KEY = "aero_network_players";

    public NetworkManager() {
        this.redisManager = new RedisManager();
        this.gson = new Gson();
    }

    public CompletableFuture<NetworkPlayer> getPlayer(UUID uuid) {
        CompletableFuture<NetworkPlayer> future = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Jedis jedis = redisManager.getJedisPool().getResource()) {
                String playerJson = jedis.hget(NETWORK_PLAYERS_KEY, uuid.toString());

                if(playerJson != null) {
                    try {
                        future.complete(gson.fromJson(playerJson, NetworkPlayer.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return future;
    }

    public CompletableFuture<NetworkPlayer> getPlayer(String name) {
        CompletableFuture<NetworkPlayer> future = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Jedis jedis = redisManager.getJedisPool().getResource()) {
                jedis.hgetAll(NETWORK_PLAYERS_KEY).forEach((uuid, playerJson) -> {
                    if(playerJson != null) {
                        try {
                            NetworkPlayer player = gson.fromJson(playerJson, NetworkPlayer.class);
                            if(player.getName().equals(name))
                                future.complete(player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        return future;
    }

    public void addPlayer(NetworkPlayer player) {
        updatePlayer(player);
    }

    public void removePlayer(UUID uuid) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Jedis jedis = redisManager.getJedisPool().getResource()) {
                jedis.hdel(NETWORK_PLAYERS_KEY, uuid.toString());
            }
        });
    }

    public void updatePlayer(NetworkPlayer player) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Jedis jedis = redisManager.getJedisPool().getResource()) {
                jedis.hset(NETWORK_PLAYERS_KEY, player.getUuid().toString(), gson.toJson(player));
            }
        });
    }

    public CompletableFuture<List<NetworkPlayer>> getAllPlayers() {
        CompletableFuture<List<NetworkPlayer>> future = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            List<NetworkPlayer> players = Lists.newArrayList();

            try (Jedis jedis = redisManager.getJedisPool().getResource()) {
                jedis.hgetAll(NETWORK_PLAYERS_KEY).forEach((uuid, player) -> {
                    try {
                        players.add(gson.fromJson(player, NetworkPlayer.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } finally {
                future.complete(players);
            }
        });

        return future;
    }
}
