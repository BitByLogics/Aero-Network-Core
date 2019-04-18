package net.aeronetwork.core.player.staff.command;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.player.staff.menu.InspectMenu;
import net.aeronetwork.core.util.FM;

public class InspectCommand extends Command {

    public InspectCommand() {
        super("inspect", "Inspect a player", "/inspect <player>", null);
        setPlayerOnly(true);
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            player.sendMessage(FM.mainFormat("Inspect", "Usage: /inspect <player>"));
        } else {
            if(AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]) != null) {
                new InspectMenu(AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0])).openInventory(player.getBukkitPlayer());
            } else {
                player.sendMessage(FM.mainFormat("Inspect", "Invalid player."));
            }
        }
    }
}
