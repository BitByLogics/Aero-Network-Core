package net.aeronetwork.core.event.packet.in;

import lombok.Getter;
import net.aeronetwork.core.event.packet.PacketEvent;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Basic event wrapper for the {@link PacketPlayInUseEntity} packet.
 */
@Getter
public class PacketInEntityUseEvent extends PacketEvent<PacketPlayInUseEntity> {

    private int entityId;
    private InteractAction interactAction;

    public PacketInEntityUseEvent(Player player, PacketPlayInUseEntity packet) {
        super(player, packet);

        try {
            Field field = packet.getClass().getDeclaredField("a");
            field.setAccessible(true);
            this.entityId = field.getInt(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.interactAction = InteractAction.valueOf(packet.a().name());
    }

    public enum InteractAction {
        ATTACK,
        INTERACT,
        INTERACT_AT
    }
}
