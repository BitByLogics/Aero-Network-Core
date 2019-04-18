package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;

public class MyCrystalsCommand extends Command {

    public MyCrystalsCommand() {
        super("mycrystals", "View your crystal balance.", "/mycrystals", null);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            player.sendMessage(FM.mainFormat("Crystals", "You have §c" + player.getCrystals() + "§e Crystal(s)."));
        }
    }
}
