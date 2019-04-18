package net.aeronetwork.core.server;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.server.settings.ServerSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;

import java.util.stream.Collectors;

@Getter
@Setter
public class ServerManager {

    private String serverName;
    private ServerType serverType;

    @Setter(AccessLevel.NONE)
    private ServerSettings serverSettings;
    private ServerEnvironment serverEnvironment;

    private Gson gson;
    private BukkitTask serverUpdater;

    public ServerManager() {
        this.serverName = "unknown";
        this.serverType = ServerType.UNKNOWN;
        this.serverSettings = new ServerSettings();
        this.serverEnvironment = new ServerEnvironment();

        this.gson = new Gson();
    }

    /**
     * Starts all server related processes.
     */
    public void start() {
        this.serverUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                try (Jedis jedis = AeroCore.REDIS_MANAGER.getJedisPool().getResource()) {
                    String json = jedis.hget(ServerConstants.SERVER_KEY, serverEnvironment.getEnv(ServerEnvironment.EnvType.AERO_ID));
                    if(json != null) {
                        AeroServer as = gson.fromJson(json, AeroServer.class);
                        as.setMaxPlayers(serverSettings.getMaxPlayers());
                        as.setPlayers(Bukkit.getServer().getOnlinePlayers().stream()
                                .map(Player::getUniqueId)
                                .collect(Collectors.toList())
                        );
                        as.setPrivateServer(serverSettings.isPrivateMode());
                        as.setJoinState(serverSettings.getJoinState());

                        jedis.hset(
                                ServerConstants.SERVER_KEY,
                                serverEnvironment.getEnv(ServerEnvironment.EnvType.AERO_ID),
                                gson.toJson(as)
                        );
                    }
                }
            }
        }.runTaskTimerAsynchronously(AeroCore.INSTANCE, 0, 20 * 5);
    }

    /**
     * Stops all server related processes.
     */
    public void stop() {
        this.serverUpdater.cancel();
    }

    /**
     * Validates whether a database update (specifically operations implementing
     * {@link net.aeronetwork.core.database.mongo.MorphiaService} are allowed to
     * proceed with certain actions.
     *
     * @return TRUE if updating is allowed, and FALSE otherwise.
     */
    public boolean validateUpdate() {
        return !serverSettings.isDisableStatTracking() && !serverSettings.isOfflineMode();
    }

    public enum ServerType {
        LOBBY,
        GAME,
        TEST,
        UNKNOWN
    }
}
