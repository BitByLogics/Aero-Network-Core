package net.aeronetwork.core.server.commands;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MOTDCommand extends Command {

    public MOTDCommand() {
        super("motd", "Modify the server MOTD", "/motd", null);

        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            if(details.getArgs().length == 0) {
                player.sendMessage(FM.mainFormat("MOTD", "MOTD Commands: "));
                player.sendMessage(FM.command("motd set <string>", "Set the MOTD of the server."));
            }

            if(details.getArgs().length >= 2) {
                if(details.getArgs()[0].equalsIgnoreCase("set")) {
                    AeroCore.SERVER_MANAGER.getServerSettings().setMotd(ChatColor.translateAlternateColorCodes('&', Util.join(1, details.getArgs())));
                    player.sendMessage(FM.mainFormat("MOTD", "Set MOTD to " + ChatColor.translateAlternateColorCodes('&', Util.join(1, details.getArgs()))));
                }
            }
        } else {
            if(details.getArgs().length == 0) {
                Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("MOTD", "MOTD Commands: "));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("motd set <string>", "Set the MOTD of the server."));
            }

            if(details.getArgs().length >= 2) {
                if(details.getArgs()[0].equalsIgnoreCase("set")) {
                    AeroCore.SERVER_MANAGER.getServerSettings().setMotd(ChatColor.translateAlternateColorCodes('&', Util.join(1, details.getArgs())));
                    Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("MOTD", "Set MOTD to " + ChatColor.translateAlternateColorCodes('&', Util.join(1, details.getArgs()))));
                }
            }
        }
    }
}
