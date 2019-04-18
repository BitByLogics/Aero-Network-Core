package net.aeronetwork.core.player.discord;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.aeronetwork.core.manager.Manager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class DiscordManager extends Manager {

    private HashMap<UUID, String> discordCodes = Maps.newHashMap();

    public DiscordManager(JavaPlugin plugin) {
        super("Discord Manager", "Handles all Discord related tasks.", plugin);
    }

    public void generateCode(AeroPlayer player) {
        if(player.getDiscordId() == null) {
            if(discordCodes.containsKey(player.getUuid())) {
                TextComponent codeCommand = new TextComponent(FM.mainFormat("Discord", "Click to copy the link command!"));
                codeCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ">link " + discordCodes.get(player.getUuid())));
                player.getBukkitPlayer().spigot().sendMessage(codeCommand);
            } else {
                String code = UUID.randomUUID().toString();
                discordCodes.put(player.getUuid(), code);
                TextComponent codeCommand = new TextComponent(FM.mainFormat("Discord", "Click to copy the link command!"));
                codeCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ">link " + discordCodes.get(player.getUuid())));
                player.getBukkitPlayer().spigot().sendMessage(codeCommand);
            }
        } else {
            player.sendMessage(FM.mainFormat("Discord", "You already have your discord linked!"));
        }
    }

    public void linkAccount(AeroPlayer player, String discordID, String discordName) {
        discordCodes.remove(player.getUuid());
        player.updateDiscordId(discordID);
        player.sendMessage(FM.mainFormat("Discord", "Successfully linked your discord to &c" + discordName + "&e!"));
    }

}
