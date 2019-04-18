package net.aeronetwork.api.util.command;

import lombok.Getter;
import net.aeronetwork.core.event.packet.in.PacketInEntityUseEvent;
import net.aeronetwork.core.util.npc.interact.InteractNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Implementation of {@link InteractNPC} that forces a player
 * to execute a command when they interact with the NPC.
 */
@Getter
public class CommandNPC extends InteractNPC {

    private String command;

    public CommandNPC(Location location, String command) {
        super(location);
        this.command = command;
    }

    public CommandNPC(Location location, String skinTexture, String skinSignature, String command) {
        super(location, skinTexture, skinSignature);
        this.command = command;
    }

    @Override
    public final void onInteract(Player player, PacketInEntityUseEvent.InteractAction action) {
        player.performCommand(this.command);
    }
}
