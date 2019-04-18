package net.aeronetwork.proxy.command;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.aeronetwork.core.auth.AeroAuth;
import net.aeronetwork.proxy.AeroProxyCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info", "");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, List<String>> serverGroupings = Maps.newConcurrentMap();
        AeroProxyCore.PROXY_MANAGER.getCachedServers().forEach(server -> {
            if(server.getServerType() != null && !server.getServerType().name().contains("PROXY")) {
                if(server.getInstance() != null) {
                    if(serverGroupings.getOrDefault(server.getInstance(), null) == null)
                        serverGroupings.put(server.getInstance(), Lists.newCopyOnWriteArrayList());
                    List<String> possibleIds = serverGroupings.get(server.getInstance());
                    possibleIds.add(server.getIds().get(0));
                }
            }
        });

        sender.sendMessage(TextComponent.fromLegacyText("§eServer Type §f-> §eServers Available"));
        if(serverGroupings.size() != 0) {
            serverGroupings.forEach((instance, ids) -> {
                sender.sendMessage(TextComponent.fromLegacyText("§6" + instance.toUpperCase() + " §f-> §a" +
                        ids.size() + " server(s) available!"));
                List<String> distinceids = ids.stream().distinct().collect(Collectors.toList());
                sender.sendMessage(TextComponent.fromLegacyText("§f -> IDs: [§e" +
                        Joiner.on("§f, §e").join(distinceids) + "§f]"));

            });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText("§cNONE!"));
        }

        sender.sendMessage(TextComponent.fromLegacyText("§eThere are currently " +
                AeroAuth.getAuthedPlayers() + " player(s) online!"));
    }
}
