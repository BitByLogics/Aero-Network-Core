package net.aeronetwork.core.redis;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.redis.command.RedisCommand;
import net.aeronetwork.core.redis.command.info.RedisCommandDetails;
import net.aeronetwork.core.redis.command.listener.RedisCommandListener;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.util.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.concurrent.Executors;

@Getter
public class RedisManager {

    @Setter
    private JedisPool jedisPool;
    private Gson gson;

    private List<RedisMessageListener> listeners;
    private List<RedisCommand> commands;

    private final String LISTENER_CHANNEL = "aero_listener_channel";

    public RedisManager() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setEvictionPolicyClassName("net.aeronetwork.core.redis.policy.RedisEvictionPolicy");
        config.setMaxTotal(25);
        config.setMaxIdle(10);
        config.setMinIdle(5);

        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(RedisManager.class.getClassLoader());
        this.jedisPool = new JedisPool(config, "162.251.166.138", 6379, 10_000,
                "170e71552b16b4f8a8ef6e0cc74ce88c8a8de298893b096b031a7c8625853873");
        Thread.currentThread().setContextClassLoader(previous);
        this.gson = new Gson();

        this.listeners = Lists.newArrayList();
        this.commands = Lists.newArrayList();

        Executors.newSingleThreadExecutor().submit(() ->
           jedisPool.getResource().subscribe(new JedisPubSub() {
               @Override
               public void onMessage(String channel, String message) {
                    handleListener(message);
               }
           }, LISTENER_CHANNEL)
        );

        registerListener(new RedisCommandListener());
    }

    /**
     * Registers a new RedisMessageListener as a valid listener.
     *
     * @param listener The listener to register.
     */
    public void registerListener(RedisMessageListener listener) {
        System.out.println("[REDIS] Registered listener " + listener.getClass().getSimpleName());
        this.listeners.add(listener);
    }

    /**
     * Publishes a new message to be sent out to all listeners.
     *
     * @param component The information to send.
     */
    public void sendListenerMessage(ListenerComponent component) {
        String json = gson.toJson(component);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(LISTENER_CHANNEL, json);
        }
    }

    /**
     * Handles all incoming Redis messages to be rerouted
     * to listeners.
     *
     * @param message The message to reroute.
     */
    public void handleListener(String message) {
        ListenerComponent component = gson.fromJson(message, ListenerComponent.class);
        listeners.forEach(l -> {
            if(l.getChannelName().equalsIgnoreCase(component.getChannel())) {
                l.onReceive(component.getMessage());
            }
        });
    }

    /**
     * Registers a new RedisCommand as a valid command.
     *
     * @param command The command to register.
     */
    public void registerCommand(RedisCommand command) {
        this.commands.add(command);
    }

    /**
     * Executes a command given the specified string.
     *
     * @param command The command to execute.
     */
    public void executeCommand(String command) {
        if(!command.isEmpty()) {
            String[] commandArray = command.split(" ");
            String[] args = (commandArray.length >= 1 ? Util.join(1, commandArray).split(" ") : new String[0]);
            RedisCommandDetails details = new RedisCommandDetails(commandArray[0], args);
            sendListenerMessage(new ListenerComponent(RedisCommandListener.COMMAND_CHANNEL, gson.toJson(details)));
        }
    }
}
