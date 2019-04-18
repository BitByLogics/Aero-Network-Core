package net.aeronetwork.core.player.punishment.menu.calls;

import lombok.Getter;

@Getter
public abstract class MenuCallback implements Runnable {

    private long length;

    public MenuCallback setLength(long length) {
        this.length = length;
        return this;
    }

}
