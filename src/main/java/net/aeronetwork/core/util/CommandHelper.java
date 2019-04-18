package net.aeronetwork.core.util;

import net.aeronetwork.core.player.AeroPlayer;
import org.bukkit.Bukkit;

import java.util.UUID;

public class CommandHelper {

    /**
     * Reply to the command sender.
     *
     * @param sender The {@link AeroPlayer} issuing the command, or null if console is issuing.
     * @param message The message being sent to the sender.
     */
    public static void replyToSender(AeroPlayer sender, String message) {
        if(sender != null) {
            sender.sendMessage(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }

    /**
     * Get the senders ID.
     *
     * @param sender The {@link AeroPlayer} issuing the command, or null if console is issuing.
     * @return The {@link AeroPlayer}'s UUID, or Console's UUID if null.
     */
    public static UUID getSenderUUID(AeroPlayer sender) {
        return sender == null ? UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670") : sender.getUuid();
    }
    
}
