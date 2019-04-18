package net.aeronetwork.proxy.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            p.sendMessage(TextComponent.fromLegacyText("§aYou are currently on server " +
                    "§e" + p.getServer().getInfo().getName()));
        }
    }
}
