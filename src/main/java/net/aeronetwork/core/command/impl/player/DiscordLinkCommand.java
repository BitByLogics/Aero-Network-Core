package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;

import java.util.Arrays;

public class DiscordLinkCommand extends Command {

    public DiscordLinkCommand() {
        super("link", "Link discord", "/link", Arrays.asList("linkdiscord", "linkdc"));
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        AeroCore.DISCORD_MANAGER.generateCode(player);
    }
}
