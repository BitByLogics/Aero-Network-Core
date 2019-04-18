package net.aeronetwork.core.event.player;

import lombok.Getter;
import net.aeronetwork.core.event.AeroEvent;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import org.bukkit.event.Cancellable;

@Getter
public class RankUpdateEvent extends AeroEvent implements Cancellable {

    private Rank rank;
    private AeroPlayer modifier;
    private AeroPlayer target;
    private UpdateType updateType;

    public RankUpdateEvent(Rank rank, AeroPlayer modifier, AeroPlayer target, UpdateType updateType) {
        this.rank = rank;
        this.modifier = modifier;
        this.target = target;
        this.updateType = updateType;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public enum UpdateType {
        ADD,
        REMOVE
    }

}
