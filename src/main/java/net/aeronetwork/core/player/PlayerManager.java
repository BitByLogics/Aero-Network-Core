package net.aeronetwork.core.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.database.mongo.MorphiaService;
import net.aeronetwork.core.database.mongo.dao.PlayerDAO;
import net.aeronetwork.core.database.mongo.dao.impl.PlayerDAOImpl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class PlayerManager {

    private MorphiaService morphiaService;
    private PlayerDAO playerDAO;
    @Getter
    private Cache<UUID, AeroPlayer> playerCache;

    public PlayerManager() {
        this.morphiaService = new MorphiaService("aero_network", AeroPlayer.class);
        this.playerDAO = new PlayerDAOImpl(AeroPlayer.class, morphiaService.getDatastore());
        this.playerCache = CacheBuilder.newBuilder().build();
    }

    public AeroPlayer getPlayer(UUID uuid) {
        try {
            return playerCache.get(uuid, () -> {
                boolean updateValid = AeroCore.SERVER_MANAGER.validateUpdate();
                AeroPlayer player = (updateValid ? playerDAO.getByUuid(uuid) : null);

                if(player == null) {
                    player = initPlayer(uuid);
                    if(player == null)
                        player = new AeroPlayer(uuid);
                }
                return player;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AeroPlayer getPlayer(String accountName) {
        CompletableFuture<AeroPlayer> futurePlayer = new CompletableFuture<>();
        playerCache.asMap().values().forEach(player -> {
            if(player.getAccountName().equalsIgnoreCase(accountName))
                futurePlayer.complete(player);
        });
        AeroPlayer p = futurePlayer.getNow(null);
        if(p == null) {
            p = (AeroCore.SERVER_MANAGER.validateUpdate() ? playerDAO.getByAccountName(accountName) : new AeroPlayer(UUID.randomUUID(), accountName));
            if(p != null) {
                playerCache.put(p.getUuid(), p);
            }
        }
        return p;
    }

    public AeroPlayer initPlayer(UUID uuid) {
        if (AeroCore.SERVER_MANAGER.validateUpdate() && playerDAO.getByUuid(uuid) == null) {
            AeroPlayer player = new AeroPlayer(uuid);
            player.setFirstLogin(System.currentTimeMillis());
            if(AeroCore.SERVER_MANAGER.validateUpdate())
                playerDAO.save(player);
            return player;
        }
        return null;
    }

    public AeroPlayer initPlayer(UUID uuid, String accountName) {
        if(AeroCore.SERVER_MANAGER.validateUpdate() && playerDAO.getByUuid(uuid) == null) {
            AeroPlayer player = new AeroPlayer(uuid, accountName);
            player.setFirstLogin(System.currentTimeMillis());
            if(AeroCore.SERVER_MANAGER.validateUpdate())
                playerDAO.save(player);
            return player;
        }
        return null;
    }

    public void initPlayer(UUID uuid, boolean cache) {
        AeroPlayer player = initPlayer(uuid);
        if(player != null && cache)
            playerCache.put(uuid, player);
    }

    public void initPlayer(UUID uuid, String accountName, boolean cache) {
        AeroPlayer player = initPlayer(uuid, accountName);
        if(player != null && cache)
            playerCache.put(uuid, player);
    }

    public void invalidateCache(UUID uuid) {
        playerCache.invalidate(uuid);
    }

    public void updateField(AeroPlayer player, String field, Object value) {
        //TODO: Check if the server is in offline mode via ServerManager
        if(AeroCore.SERVER_MANAGER.validateUpdate())
            Executors.newSingleThreadExecutor().submit(() ->
                    playerDAO.updateField(player, field, value));
    }
}
