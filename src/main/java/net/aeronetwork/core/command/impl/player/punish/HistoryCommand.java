package net.aeronetwork.core.command.impl.player.punish;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.menu.PunishmentHistoryMenu;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        super("history", "View a players punishment history.", "/history <player>", null);
        setRank(Rank.STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            if(details.getArgs().length >= 1) {
                AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

                if(target != null) {
                    new PunishmentHistoryMenu(target).openInventory(Bukkit.getPlayer(player.getUuid()));
                } else {
                    player.sendMessage(FM.mainFormat("Punish", "Invalid player."));
                }
            } else {
                player.sendMessage(FM.mainFormat("Punish", "Usage: /history <player>"));
            }
        }
    }
}
