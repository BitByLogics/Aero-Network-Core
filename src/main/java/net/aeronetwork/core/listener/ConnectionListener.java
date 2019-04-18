package net.aeronetwork.core.listener;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.data.PunishmentData;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.nms.PacketInterceptorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Date;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        AeroCore.PLAYER_MANAGER.initPlayer(e.getUniqueId(), e.getName(), true);
        AeroPlayer player = AeroCore.PLAYER_MANAGER.getPlayer(e.getUniqueId());
        String IP = e.getAddress().getHostAddress().replace("/", "").replace(":", "");

        if(!player.getIps().contains(IP)) {
            player.getIps().add(IP);
            player.updateIPList();
        }

        PunishmentData data = AeroCore.PUNISHMENT_MANAGER.refreshAndGet(e.getUniqueId());
        if(data.isBanned()) {
            // PUT IN CONFIGURATION SOMEWHERE IN THE FUTURE (JSON maybe).
            String message = new StringBuilder("§cYou are currently banned for")
                    .append("\n§e")
                    .append(data.getBanData().getReason())
                    .append("\n\n§cThe ban will expire on ")
                    .append((data.getBanData().isPermanent() ? "§e- (PERMANENT)" : "§e" +
                            AeroCore.PUNISHMENT_MANAGER.getDateFormat()
                                    .format(new Date(data.getBanData().getEndTime()))))
                    .append("\n\n§cBelieve the punishment was unfair? Appeal here:\n")
                    .append(data.getBanData().isAppealAllowed() ? "§eFORUMS" : "§eNOT ALLOWED (auto deny if appealed)")
                    .toString();
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
        }

        if(AeroCore.SERVER_MANAGER.validateUpdate())
            AeroCore.PLAYER_MANAGER.updateField(player, "accountName", e.getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onConnect(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        AeroPlayer player = AeroCore.PLAYER_MANAGER.getPlayer(p.getUniqueId());

        if(player.isVanished()) {
            if(player.getRank().getPriority() <= Rank.YT.getPriority()) {
                for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                    if(bukkitPlayer != p && bukkitPlayer.canSee(player.getBukkitPlayer())) {
                        AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(bukkitPlayer.getUniqueId());
                        if(target.getRank().getRankType().equals(player.getRank().getRankType())
                                || target.getRank().getRankType().equals(Rank.RankType.HIGH_STAFF)
                                || target.getRank().getPriority() < player.getRank().getPriority()) {

                        } else {
                            bukkitPlayer.hidePlayer(player.getBukkitPlayer());
                        }
                    }
                }
                player.sendMessage(FM.mainFormat("Vanish", "You joined silently."));
            } else {
                player.setVanished(false);
                player.sendMessage(FM.mainFormat("Vanish", "You no longer have permission to vanish, you're now unvanished."));
            }
        }

        for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
            AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(bukkitPlayer.getUniqueId());
            if(target.isVanished()) {
                if(bukkitPlayer != p && p.canSee(bukkitPlayer)) {
                    if(player.getRank().getRankType().equals(target.getRank().getRankType())
                            || player.getRank().getRankType().equals(Rank.RankType.HIGH_STAFF)
                            || target.getRank().getPriority() > player.getRank().getPriority()) {

                    } else {
                        p.hidePlayer(bukkitPlayer);
                    }
                }
            }
        }

        e.setJoinMessage("");

        PermissionAttachment attachment = p.addAttachment(AeroCore.INSTANCE);

        if(player.isDisguised()) {
            AeroCore.DISGUISE_MANAGER.redisguise(player);
        }

        if(player.isInStaffMode()) {
            if(player.getRank().getPriority() <= Rank.JR_STAFF.getPriority()) {
                player.sendMessage(FM.mainFormat("Staff Mode", "You've logged in while in staff mode."));
                AeroCore.STAFF_MANAGER.enterStaffMode(player);
            } else {
                player.sendMessage(FM.mainFormat("Staff Mode", "You no longer have permission to be in staff mode, you're no longer in staff mode."));
                player.setInStaffMode(false);
            }
        }

        player.getRanks().forEach(rank -> rank.getPermissions().forEach(perm -> attachment.setPermission(perm, true)));

        if(player.getRank().getPriority() <= Rank.ADMIN.getPriority())
            p.setOp(true);

        String motd = AeroCore.SERVER_MANAGER.getServerSettings().getMotd();
        if(motd != null)
            p.sendMessage(motd);

        PacketInterceptorUtil.inject(p);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage("");
        AeroCore.PLAYER_MANAGER.invalidateCache(p.getUniqueId());

        PacketInterceptorUtil.eject(p);
    }
}
