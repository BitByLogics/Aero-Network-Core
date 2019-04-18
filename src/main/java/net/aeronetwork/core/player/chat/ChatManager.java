package net.aeronetwork.core.player.chat;

import com.google.common.collect.Lists;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.event.player.ChatEvent;
import net.aeronetwork.core.manager.Manager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.disguise.DisguiseData;
import net.aeronetwork.core.player.punishment.data.PunishmentData;
import net.aeronetwork.core.player.punishment.player.MutedPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ChatManager extends Manager {

    private List<String> blacklistedCommands = Lists.newArrayList();

    public ChatManager(JavaPlugin plugin) {
        super("Chat Manager", "Handle all chat related tasks.", plugin);
        loadBlacklistedCommands();
    }

    private void loadBlacklistedCommands() {
        blacklistedCommands.add("/pl");
        blacklistedCommands.add("/plugins");
        blacklistedCommands.add("/?");
        blacklistedCommands.add("/ver");
        blacklistedCommands.add("/version");
        blacklistedCommands.add("/me");
        blacklistedCommands.add("/bukkit:pl");
        blacklistedCommands.add("/bukkit:plugins");
        blacklistedCommands.add("/bukkit:?");
        blacklistedCommands.add("/bukkit:ver");
        blacklistedCommands.add("/bukkit:version");
        blacklistedCommands.add("/minecraft:me");
        blacklistedCommands.add("/minecraft:help");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(blacklistedCommands.stream().anyMatch(command -> command.equalsIgnoreCase(event.getMessage().trim().split(" ")[0]))
                && AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId()).getRank().getRankType() != Rank.RankType.HIGH_STAFF) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(FM.mainFormat("Permissions", "&cYou cannot use this command."));
        }

        if(event.getMessage().trim().split(" ")[0].equalsIgnoreCase("/bukkit:help") || event.getMessage().trim().split(" ")[0].equalsIgnoreCase("/minecraft:help")) {
            event.setMessage("/help");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        AeroPlayer aeroPlayer = AeroCore.PLAYER_MANAGER.getPlayer(p.getUniqueId());
        event.setCancelled(true);

        if(aeroPlayer == null) {
            event.getPlayer().sendMessage(FM.mainFormat(
                    "ERROR",
                    "An error occurred while trying to retrieve your player data! " +
                            "Please rejoin the server to fix this issue. " +
                            "If the issue persists, please contact a staff member.")
            );
            return;
        }

        ChatEvent chatEvent = new ChatEvent(aeroPlayer, event.getMessage());
        Bukkit.getPluginManager().callEvent(chatEvent);

        if(!chatEvent.isCancelled()) {
            // Mute stuff
            PunishmentData data = AeroCore.PUNISHMENT_MANAGER.getPunishmentData(p.getUniqueId());
            if(data.isMuted()) {
                MutedPlayer mutedPlayer = data.getMuteData();
                if(System.currentTimeMillis() <= mutedPlayer.getEndTime()) {
                    String message = new StringBuilder()
                            .append("§cYou are currently muted for §e")
                            .append(mutedPlayer.getReason())
                            .append("\n§cYour mute will expire on §e")
                            .append(mutedPlayer.isPermanent() ? "- (PERMANENT)" :
                                    AeroCore.PUNISHMENT_MANAGER.getDateFormat().format(mutedPlayer.getEndTime()))
                            .append("\n§cBelieve the punishment was unfair? Appeal here:\n")
                            .append("§e")
                            .append(mutedPlayer.isAppealAllowed() ? "FORUMS" : "NOT ALLOWED (auto deny if appealed)")
                            .toString();
                    p.sendMessage(message);
                    return;
                } else {
                    AeroCore.PUNISHMENT_MANAGER.refreshAndGet(p.getUniqueId());
                }
            }

            DisguiseData disguiseData = aeroPlayer.getDisguiseData() != null ? aeroPlayer.getDisguiseData() :
                    new DisguiseData(aeroPlayer.getAccountName(), aeroPlayer.getAccountName(), Rank.DEFAULT);

            Rank rank = aeroPlayer.isDisguised() ? disguiseData.getRank() : aeroPlayer.getRank();
            String name = aeroPlayer.isDisguised() ? disguiseData.getName() : aeroPlayer.getAccountName();

            Bukkit.getOnlinePlayers().forEach(otherPlayer -> otherPlayer.sendMessage(rank.getPrefix() + name +
                    rank.getSuffix() + (rank.equals(Rank.DEFAULT) ? " §7" : " §f") + event.getMessage()));
        }
    }

}
