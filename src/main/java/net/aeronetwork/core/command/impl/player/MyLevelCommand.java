package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;

import java.util.Arrays;

public class MyLevelCommand extends Command {

    public MyLevelCommand() {
        super("mylevel", "View your level information.", "/mylevel", Arrays.asList("myexp"));
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            player.sendMessage(FM.mainFormat("Exp", "Your current level is §c" + AeroCore.EXPERIENCE_MANAGER.calculateLevel(player.getExperience()) + " §8(§c" + player.getExperience() + "§8)§e."));
        }
    }
}
