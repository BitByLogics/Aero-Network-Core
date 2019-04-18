package net.aeronetwork.proxy.redis;

import net.aeronetwork.core.redis.listener.RedisMessageListener;

public class ProxyPipelineListener extends RedisMessageListener {

    public ProxyPipelineListener() {
        super("proxy_main_pipeline");
    }

    @Override
    public void onReceive(String message) {

    }
}
