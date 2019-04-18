package net.aeronetwork.proxy;

import net.aeronetwork.core.auth.AeroAuth;
import net.aeronetwork.core.redis.RedisManager;
import net.aeronetwork.proxy.command.InfoCommand;
import net.aeronetwork.proxy.command.LobbyCommand;
import net.aeronetwork.proxy.command.WhereAmICommand;
import net.aeronetwork.proxy.listener.ConnectionListener;
import net.aeronetwork.proxy.listener.ServerKickListener;
import net.aeronetwork.proxy.redis.listener.ConnectListener;
import net.aeronetwork.proxy.redis.listener.DisconnectListener;
import net.md_5.bungee.api.plugin.Plugin;

public class AeroProxyCore extends Plugin {

    public static AeroProxyCore INSTANCE;
    public static RedisManager REDIS_MANAGER = new RedisManager();
    public static ProxyManager PROXY_MANAGER = new ProxyManager();
//    private RedisMessageListener proxyPipeline;

    public void onEnable() {
        INSTANCE = this;

        PROXY_MANAGER.start();
        AeroAuth.invalidateAll();
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new ServerKickListener());

        getProxy().getPluginManager().registerCommand(this, new InfoCommand());
        getProxy().getPluginManager().registerCommand(this, new LobbyCommand());
        getProxy().getPluginManager().registerCommand(this, new WhereAmICommand());

        REDIS_MANAGER.registerListener(new ConnectListener());
        REDIS_MANAGER.registerListener(new DisconnectListener());
    }
}
