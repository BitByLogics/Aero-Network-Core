package net.aeronetwork.core.command.impl;

import net.aeronetwork.core.NetworkCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.CommandSender;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Get help information.", "help", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("----------------------------------------");
        NetworkCore.COMMAND_MANAGER.getCommands().forEach(command ->
            sender.sendMessage(command.getName() + " - " + command.getDescription()));
        sender.sendMessage("----------------------------------------");
    }
}
