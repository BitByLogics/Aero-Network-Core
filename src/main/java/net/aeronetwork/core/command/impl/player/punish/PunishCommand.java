package net.aeronetwork.core.command.impl.player.punish;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.menu.MainPunishMenu;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;
import org.bukkit.Bukkit;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish", "Punish players.", "/punish", null);
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            if(details.getArgs().length >= 2) {
                AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[0]);

                if(target != null) {
                    MainPunishMenu punishMenu = new MainPunishMenu(target.getAccountName(), Util.join(1, details.getArgs()));
                    punishMenu.openInventory(Bukkit.getPlayer(player.getUuid()));
                } else {
                    player.sendMessage(FM.mainFormat("Punish", "Invalid player."));
                }
            } else {
                player.sendMessage(FM.mainFormat("Punish", "Usage: /punish <player> <reason>"));
            }
        }
    }
}
