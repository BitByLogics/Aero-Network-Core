package net.aeronetwork.api.util;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageHelper {

    /**
     * Send a delayed message.
     *
     * @param player Player receiving the message.
     * @param message Message being sent.
     * @param time Time until message is sent.
     * @param timeUnit TimeUnit in which the message is sent.
     */
    public static void sendDelayedMessage(Player player, String message, long time, TimeUnit timeUnit) {
        Executors.newScheduledThreadPool(0).schedule(
                () -> player.sendMessage(FM.translate(message)),
                time,
                timeUnit
        );
    }

    /**
     * Send a delayed message with an end task.
     *
     * @param player Player receiving the message.
     * @param message Message being sent.
     * @param time Time until message is sent.
     * @param timeUnit TimeUnit for time integer.
     * @param endTask Task ran when message is sent.
     */
    public static void sendDelayedMessage(Player player, String message, long time, TimeUnit timeUnit, Runnable endTask) {
        Executors.newScheduledThreadPool(0).schedule(
                () -> {
                    player.sendMessage(FM.translate(message));
                    endTask.run();
                },
                time,
                timeUnit
        );
    }

    /**
     * Send delayed messages.
     *
     * @param player Player receiving the messages.
     * @param time Time between each message.
     * @param timeUnit TimeUnit in between each message.
     * @param messages Messages being sent.
     * @param delay Delay until first message is sent.
     * @param delayUnit TimeUnit for delay integer.
     */
    public static void sendDelayedMessages(Player player, long time, TimeUnit timeUnit, List<String> messages, int delay, TimeUnit delayUnit) {
        AtomicInteger currentMessage = new AtomicInteger(0);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
        service.scheduleAtFixedRate(
                () -> {
                    if(currentMessage.get() < messages.size()) {
                        player.sendMessage(messages.get(currentMessage.getAndIncrement()));
                    } else {
                        service.shutdown();
                    }
                },
                delay,
                time,
                timeUnit
        );
    }

    /**
     * Send delayed messages with an end task.
     *
     * @param player Player receiving the messages.
     * @param time Time between each message.
     * @param timeUnit TimeUnit in between each message.
     * @param messages Messages being sent.
     * @param delay Delay until first message is sent.
     * @param delayUnit TimeUnit for delay integer.
     * @param endTask Task ran when all messages are sent.
     */
    public static void sendDelayedMessages(Player player, long time, TimeUnit timeUnit, List<String> messages, int delay, TimeUnit delayUnit, Runnable endTask) {
        AtomicInteger currentMessage = new AtomicInteger(0);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
        service.scheduleAtFixedRate(
                () -> {
                    if(currentMessage.get() < messages.size()) {
                        player.sendMessage(messages.get(currentMessage.getAndIncrement()));
                    } else {
                        endTask.run();
                        service.shutdown();
                    }
                },
                delay,
                time,
                timeUnit
        );
    }
}
