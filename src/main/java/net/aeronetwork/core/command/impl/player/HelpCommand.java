package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "View help.", "/help", null);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            player.sendMessage(FM.mainFormat("Help", "Server Help"));
            player.sendMessage("§c§lDiscord §8- §ewww.aeronetwork.net/discord");
            player.sendMessage("§c§lForums §8- §ewww.aeronetwork.net");
            player.sendMessage("§c§lTwitter §8- §ewww.twitter.com/AeroNetworkMC");
        }
    }
}
