package net.aeronetwork.proxy;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Getter;
import net.aeronetwork.core.server.AeroServer;
import net.aeronetwork.core.server.ServerConstants;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class ProxyManager {

    private List<AeroServer> cachedServers;
    private Gson gson;

    public ProxyManager() {
        this.cachedServers = Lists.newArrayList();
        this.gson = new Gson();
    }

    public void start() {
        ProxyServer.getInstance().getScheduler().schedule(AeroProxyCore.INSTANCE, () -> {
            Map<String, String> servers;
            try(Jedis jedis = AeroProxyCore.REDIS_MANAGER.getJedisPool().getResource()) {
                servers = jedis.hgetAll(ServerConstants.SERVER_KEY);
            }

            if(servers != null) {
                this.cachedServers = servers.entrySet().stream()
                        .map(entry -> gson.fromJson(entry.getValue(), AeroServer.class))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                rebuildProxyServerCache();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public List<AeroServer> getServersByInstance(String instance) {
        return this.cachedServers.stream()
                .filter(server -> server.getInstance().equals(instance))
                .collect(Collectors.toList());
    }

    public List<AeroServer> getServersById(String id) {
        return this.cachedServers.stream()
                .filter(server -> server.getIds().contains(id))
                .collect(Collectors.toList());
    }

    public AeroServer getPriorityServerByInstance(String instance) {
        List<AeroServer> servers = getServersByInstance(instance);
        CompletableFuture<AeroServer> priority = new CompletableFuture<>();
        servers.forEach(server -> {
            if(server.getJoinState() == AeroServer.JoinState.JOINABLE && (priority.getNow(null) == null ||
                    server.getPlayers().size() > priority.getNow(null).getPlayers().size())) {
                priority.complete(server);
            }
        });
        return priority.getNow(null);
    }

    public AeroServer getPriorityServerById(String id) {
        List<AeroServer> servers = getServersById(id);
        CompletableFuture<AeroServer> priority = new CompletableFuture<>();
        servers.forEach(server -> {
            if(server.getJoinState() == AeroServer.JoinState.JOINABLE && ((priority.getNow(null) == null ||
                    (server.getPlayers().size() > priority.getNow(null).getPlayers().size())))) {
                priority.complete(server);
            }
        });
        return priority.getNow(null);
    }

    public ServerInfo constructServerInfo(AeroServer server) {
        return ProxyServer.getInstance().constructServerInfo(
                server.getAeroId(),
                Util.getAddr(server.getIp() + ":" + server.getBoundPort()),
                "§c§lAero Network",
                false
        );
    }

    public void rebuildProxyServerCache() {
        List<String> removeNames = Lists.newArrayList();
        ProxyServer.getInstance().getServers().forEach((name, server) -> {
            if(!server.getName().equalsIgnoreCase("fallback"))
                removeNames.add(name);
        });
        removeNames.forEach(ProxyServer.getInstance().getServers()::remove);

        this.cachedServers.forEach(server -> {
            if(server.getServerType() != null && !server.getServerType().name().contains("PROXY"))
                ProxyServer.getInstance().getServers().put(server.getAeroId(), constructServerInfo(server));
        });
    }

    public void connectPlayer(ProxiedPlayer player, String id, boolean notify) {
        AeroServer server = AeroProxyCore.PROXY_MANAGER.getPriorityServerById(id);
        if(server != null) {
            connectPlayer(player, server, notify);
        } else {
            player.disconnect(TextComponent.fromLegacyText("§cCould not find a server to connect you to!"));
        }
    }

    public void connectPlayer(ProxiedPlayer player, AeroServer server) {
        connectPlayer(player, server, true);
    }

    public void connectPlayer(ProxiedPlayer player, AeroServer server, boolean notify) {
        ServerInfo info = constructServerInfo(server);
        player.connect(info);

        if(notify) {
            player.sendMessage(TextComponent.fromLegacyText("§7Connecting you to §e" + server.getAeroId() + "§7!"));
        }
    }
}
