package net.aeronetwork.core.util;

import net.aeronetwork.core.AeroCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(AeroCore.INSTANCE, () -> player.sendMessage(FM.translate(message)), 20 * timeUnit.toSeconds(time));
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(AeroCore.INSTANCE, () -> { player.sendMessage(FM.translate(message)); endTask.run(); }, 20 * timeUnit.toSeconds(time));
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if(currentMessage.get() < messages.size()) {
                    player.sendMessage(messages.get(currentMessage.getAndIncrement()));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(AeroCore.INSTANCE, 20 * delayUnit.toSeconds(delay), 20 * timeUnit.toSeconds(time));
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if(currentMessage.get() < messages.size()) {
                    player.sendMessage(messages.get(currentMessage.getAndIncrement()));
                } else {
                    endTask.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(AeroCore.INSTANCE, 20 * delayUnit.toSeconds(delay), 20 * timeUnit.toSeconds(time));
    }

}
