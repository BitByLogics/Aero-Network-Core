package net.aeronetwork.core.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.annotation.SubCommand;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.command.info.CommandInfo;
import net.aeronetwork.core.command.obj.CommandTemplate;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.server.ServerManager;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Manages registering, and dispatching of commands.
 */
public class CommandManager {

    private List<CommandInfo> commands;
    private CommandMap commandMap;

    public CommandManager() {
        this.commands = Lists.newArrayList();

        try {
            Class clazz = Class.forName("org.bukkit.craftbukkit.v1_8_R3.CraftServer");
            Field field = clazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            this.commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the specified command as a valid command
     * internally as an Aero specific command, and a valid
     * Bukkit/Spigot command.
     *
     * @param command The command class to register.
     */
    public void registerCommand(Class<? extends Command> command) {
        try {
            Command c = command.newInstance();

            Map<SubCommand, Method> subCommands = Maps.newConcurrentMap();
            Arrays.stream(command.getMethods()).forEach(method -> {
                if(method.isAnnotationPresent(SubCommand.class) && method.getParameterCount() == 2) {
                    List<Class> params = Lists.newArrayList(method.getParameterTypes());
                    if(params.contains(AeroPlayer.class) && params.contains(CommandDetails.class)) {
                        subCommands.put(method.getAnnotation(SubCommand.class), method);
                    } else {
                        throw new UnsupportedOperationException("Method is labeled as a sub command, " +
                                "but doesn't have valid parameters: " + command.getName() + "#" + method.getName());
                    }
                }
            });

            CommandInfo info = new CommandInfo(c.getName(), c.getAliases(), command, subCommands);
            commands.add(info);

            commandMap.register("aerocommand",
                    new CommandTemplate(c.getName(), c.getDescription(), c.getUsage(), c.getAliases()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the specified commands as valid commands
     * internally as Aero specific commands, and as valid
     * Bukkit/Spigot commands.
     *
     * @param commands The commands to register.
     */
    @SafeVarargs
    public final void registerCommands(Class<? extends Command>... commands) {
        if(commands != null)
            Arrays.stream(commands).forEach(this::registerCommand);
    }

    /**
     * Dispatches the appropriate command based on the specified
     * values.
     *
     * @param sender The issuer of the command.
     * @param command The command that was executed.
     * @param args All arguments associated with the command.
     */
    public void executeCommand(CommandSender sender, String command, String[] args) {
        CommandInfo info = commands.stream()
                .filter(c -> c.getName().equalsIgnoreCase(command) ||
                        c.getAliases().stream().
                                filter(alias -> alias.equalsIgnoreCase(command))
                                .findFirst().orElse(null) != null)
                .findFirst()
                .orElse(null);
        if(info != null) {
            Command c;
            try {
                c = info.getCommandClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            AeroPlayer player = (sender instanceof ConsoleCommandSender ? null : AeroCore.PLAYER_MANAGER.getPlayer(((Player) sender).getUniqueId()));

            if(player == null && c.isPlayerOnly()) {
                sender.sendMessage("§cOnly players can use this command.");
                return;
            }

            if(args.length >= 1) {
                Method subMethod = info.getSubCommandMethod(args[0]);
                if(subMethod != null) {
                    String[] newArgs = args.length >= 2 ? Util.join(1, args).split(" ") : new String[] {};
                    SubCommand sc = subMethod.getAnnotation(SubCommand.class);

                    // Permission checking
                    if(player == null && sc.playerOnly()) {
                        sender.sendMessage("§cConsole can't use this command!");
                        return;
                    } else if(player != null && (sc.rankRequired().getPriority() < player.getRank().getPriority() ||
                            (sc.allowOp() && !sender.isOp()))) {
                        sender.sendMessage(FM.mainFormat("Permissions", "&cYou cannot use this command."));
                        return;
                    }

                    if (player != null && !c.isAllowedInGame() && AeroCore.SERVER_MANAGER.getServerType() == ServerManager.ServerType.GAME
                            && player.getRank().getRankType() != Rank.RankType.HIGH_STAFF) {
                        sender.sendMessage(FM.mainFormat("Permissions", "&cYou cannot run this command in-game."));
                        return;
                    }

                    // Arg checking
                    if(sc.minArgs() != CommandConstants.IGNORE && newArgs.length < sc.minArgs()) {
                        sender.sendMessage("§cNot enough args! " + sc.usage() + " (min " + sc.minArgs() + ")");
                        return;
                    }

                    if(sc.maxArgs() != CommandConstants.IGNORE && newArgs.length > sc.maxArgs()) {
                        sender.sendMessage("§cToo many args! " + sc.usage() + " (max " + sc.maxArgs() + ")");
                        return;
                    }

                    try {
                        subMethod.invoke(c, player, new CommandDetails(command, newArgs));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

            // Main command if sub command fails
            if(sender instanceof ConsoleCommandSender && c.isPlayerOnly()) {
                sender.sendMessage("§cConsole can't use this command!");
                return;
            } else if(player != null && (player.getRank().getPriority() > c.getRank().getPriority() ||
                    (c.isAllowOp() && !sender.isOp()))) {
                sender.sendMessage(FM.mainFormat("Permissions", "&cYou cannot use this command."));
                return;
            }

            if (player != null && !c.isAllowedInGame() && AeroCore.SERVER_MANAGER.getServerType() == ServerManager.ServerType.GAME
                    && player.getRank().getRankType() != Rank.RankType.HIGH_STAFF) {
                sender.sendMessage(FM.mainFormat("Permissions", "&cYou cannot run this command in-game."));
                return;
            }

            c.execute(player, new CommandDetails(command, args));
        }
    }
}
