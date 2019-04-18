package net.aeronetwork.core.event.player;

import net.aeronetwork.core.event.AeroEvent;
import net.aeronetwork.core.player.AeroPlayer;
import org.bukkit.event.Cancellable;

public class ChatEvent extends AeroEvent implements Cancellable {

    private boolean cancelled;
    private AeroPlayer player;
    private String message;

    public ChatEvent(AeroPlayer player, String message) {
        this.player = player;
        this.message = message;
    }

    public AeroPlayer getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
