package net.aeronetwork.core.command.obj;

import net.aeronetwork.core.AeroCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The base template used to link the Bukkit command
 * system to the Aero command system.
 */
public class CommandTemplate extends Command {

    public CommandTemplate(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        AeroCore.COMMAND_MANAGER.executeCommand(sender, command, args);
        return true;
    }
}
