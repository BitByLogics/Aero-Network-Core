package net.aeronetwork.core.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Defines the base for all custom Aero related events piped
 * through Bukkit's event system.
 */
public abstract class AeroEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
