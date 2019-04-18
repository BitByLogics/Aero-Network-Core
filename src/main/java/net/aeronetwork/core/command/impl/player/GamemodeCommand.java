package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.annotation.SubCommand;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode", "Change your gamemode.", "/gamemode <gamemode> [player]", Arrays.asList("gm"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            player.sendMessage(FM.mainFormat("Gamemode", "Usage: /gamemode <gamemode> [player]"));
        }
    }

    @SubCommand(name = "creative", usage = "/gamemode creative", desc = "Gamemode Creative", rankRequired = Rank.ADMIN, aliases = {"c", "1"})
    public void creative(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            Bukkit.getPlayer(player.getUuid()).setGameMode(GameMode.CREATIVE);
            player.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cCreative§e!"));
        } else {
            Player target = Bukkit.getPlayer(details.getArgs()[0]);

            if(target != null) {
                target.setGameMode(GameMode.CREATIVE);
                target.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cCreative§e!"));
                player.sendMessage(FM.mainFormat("Gamemode", "Set §c" + target.getName() + "§e's gamemode to §cCreative§e!"));
            } else {
                player.sendMessage(FM.mainFormat("Gamemode", "That player is offline."));
            }
        }
    }

    @SubCommand(name = "survival", usage = "/gamemode survival", desc = "Gamemode Survival", rankRequired = Rank.ADMIN, aliases = {"s", "0"})
    public void survival(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            Bukkit.getPlayer(player.getUuid()).setGameMode(GameMode.SURVIVAL);
            player.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cSurvival§e!"));
        } else {
            Player target = Bukkit.getPlayer(details.getArgs()[0]);

            if(target != null) {
                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cSurvival§e!"));
                player.sendMessage(FM.mainFormat("Gamemode", "Set §c" + target.getName() + "§e's gamemode to §cSurvival§e!"));
            } else {
                player.sendMessage(FM.mainFormat("Gamemode", "That player is offline."));
            }
        }
    }

    @SubCommand(name = "adventure", usage = "/gamemode adventure", desc = "Gamemode Adventure", rankRequired = Rank.ADMIN, aliases = {"a", "2"})
    public void adventure(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            Bukkit.getPlayer(player.getUuid()).setGameMode(GameMode.ADVENTURE);
            player.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cAdventure§e!"));
        } else {
            Player target = Bukkit.getPlayer(details.getArgs()[0]);

            if(target != null) {
                target.setGameMode(GameMode.ADVENTURE);
                target.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cAdventure§e!"));
                player.sendMessage(FM.mainFormat("Gamemode", "Set §c" + target.getName() + "§e's gamemode to §cAdventure§e!"));
            } else {
                player.sendMessage(FM.mainFormat("Gamemode", "That player is offline."));
            }
        }
    }

    @SubCommand(name = "spectator", usage = "/gamemode spectator", desc = "Gamemode Spectator", rankRequired = Rank.ADMIN, aliases = {"sp", "3"})
    public void spectator(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            Bukkit.getPlayer(player.getUuid()).setGameMode(GameMode.SPECTATOR);
            player.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cSpectator§e!"));
        } else {
            Player target = Bukkit.getPlayer(details.getArgs()[0]);

            if(target != null) {
                target.setGameMode(GameMode.SPECTATOR);
                target.sendMessage(FM.mainFormat("Gamemode", "Your gamemode has been updated to §cSpectator§e!"));
                player.sendMessage(FM.mainFormat("Gamemode", "Set §c" + target.getName() + "§e's gamemode to §cSpectator§e!"));
            } else {
                player.sendMessage(FM.mainFormat("Gamemode", "That player is offline."));
            }
        }
    }
}
