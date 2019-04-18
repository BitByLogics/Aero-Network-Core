package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class ExpCommand extends Command {

    public ExpCommand() {
        super("exp", "Manage player experience.", "/exp", Arrays.asList("experience"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            if(details.getArgs().length == 0) {
                player.sendMessage(FM.mainFormat("Exp", "Exp Commands: "));
                player.sendMessage(FM.command("exp level <player>", "View a player's exp level."));
                player.sendMessage(FM.command("exp calculate <level>", "Calculate how much exp is needed for a level."));
                player.sendMessage(FM.command("exp add <player> <amount>", "Add exp to a player's account."));
                player.sendMessage(FM.command("exp remove <player> <amount>", "Remove exp from a player's account."));
                player.sendMessage(FM.command("exp set <player> <level>", "Set a player's exp level."));
            }

            if(details.getArgs().length == 2) {
                if(details.getArgs()[0].equalsIgnoreCase("level")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        player.sendMessage(FM.mainFormat("Exp", "§c" + target.getAccountName() + "§e's EXP level is §c" + AeroCore.EXPERIENCE_MANAGER.calculateLevel(target.getExperience()) + " §8(§c" + target.getExperience() + "§8)§e."));
                    } else {
                        player.sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("calculate")) {
                    if(NumberUtils.isNumber(details.getArgs()[1])) {
                        player.sendMessage(FM.mainFormat("Exp", "Exp required for level §c" + details.getArgs()[1] + "§e is §c" + AeroCore.EXPERIENCE_MANAGER.getExperienceForLevel(Long.valueOf(details.getArgs()[1]) - 1) + "§e."));
                    } else {
                        player.sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                    }
                }
            }

            if(details.getArgs().length >= 3) {
                if(details.getArgs()[0].equalsIgnoreCase("add")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(target.getExperience() + Long.valueOf(details.getArgs()[2]));
                            player.sendMessage(FM.mainFormat("Exp", "Successfully added §c" + details.getArgs()[2] + "§e to §c" + target.getAccountName() + "§e's experience."));
                        } else {
                            player.sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        player.sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("remove")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(target.getExperience() - Long.valueOf(details.getArgs()[2]));
                            player.sendMessage(FM.mainFormat("Exp", "Successfully removed §c" + details.getArgs()[2] + "§e from §c" + target.getAccountName() + "§e's experience."));
                        } else {
                            player.sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        player.sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("set")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(Long.valueOf(details.getArgs()[2]));
                            player.sendMessage(FM.mainFormat("Exp", "Successfully set §c" + target.getAccountName() + "§e's experience to §c" + details.getArgs()[2] + "§e."));
                        } else {
                            player.sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        player.sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }
            }
        } else {
            if(details.getArgs().length == 0) {
                Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Exp Commands: "));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("exp level <player>", "View a player's exp level."));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("exp calculate <level>", "Calculate how much exp is needed for a level."));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("exp add <player> <amount>", "Add exp to a player's account."));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("exp remove <player> <amount>", "Remove exp from a player's account."));
                Bukkit.getServer().getConsoleSender().sendMessage(FM.command("exp set <player> <level>", "Set a player's exp level."));
            }

            if(details.getArgs().length == 2) {
                if(details.getArgs()[0].equalsIgnoreCase("level")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "§c" + target.getAccountName() + "§e's EXP level is §c" + AeroCore.EXPERIENCE_MANAGER.calculateLevel(target.getExperience()) + " §8(§c" + target.getExperience() + "§8)§e."));
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("calculate")) {
                    if(NumberUtils.isNumber(details.getArgs()[1])) {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Exp required for level §c" + details.getArgs()[1] + "§e is §c" + AeroCore.EXPERIENCE_MANAGER.getExperienceForLevel(Long.valueOf(details.getArgs()[1])) + "§e."));
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                    }
                }
            }

            if(details.getArgs().length >= 3) {
                if(details.getArgs()[0].equalsIgnoreCase("add")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(target.getExperience() + Long.valueOf(details.getArgs()[2]));
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Successfully added §c" + details.getArgs()[2] + "§e to §c" + target.getAccountName() + "§e's experience."));
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("remove")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(target.getExperience() - Long.valueOf(details.getArgs()[2]));
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Successfully removed §c" + details.getArgs()[2] + "§e from §c" + target.getAccountName() + "§e's experience."));
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }

                if(details.getArgs()[0].equalsIgnoreCase("set")) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(details.getArgs()[1]);

                    if(target != null) {
                        if(NumberUtils.isNumber(details.getArgs()[2])) {
                            target.updateExperience(target.getExperience() + Long.valueOf(details.getArgs()[2]));
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Successfully set §c" + target.getAccountName() + "§e's experience to §c" + details.getArgs()[2] + "§e."));
                        } else {
                            Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "You must provide a number."));
                        }
                    } else {
                        Bukkit.getServer().getConsoleSender().sendMessage(FM.mainFormat("Exp", "Invalid player."));
                    }
                }
            }
        }
    }
}
