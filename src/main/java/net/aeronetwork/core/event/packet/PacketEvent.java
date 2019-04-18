package net.aeronetwork.core.event.packet;

import lombok.Getter;
import net.aeronetwork.core.event.AeroEvent;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

/**
 * Defines the base for all Aero related packet events.
 *
 * All implementations of this class will most likely use
 * heavy amounts of NMS (net.minecraft.server), resulting
 * in all derivations, and inheriting objects to be extremely
 * volatile if there is a major version change.
 *
 * @param <T> The type of packet the inheriting object is associated
 *           to.
 */
@Getter
public abstract class PacketEvent<T extends Packet> extends AeroEvent {

    private Player player;
    private T packet;
    private boolean intercept;

    public PacketEvent(Player player, T packet) {
        this.player = player;
        this.packet = packet;
        this.intercept = false;
    }
}
