package net.aeronetwork.core.player.punishment;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.database.mongo.MorphiaService;
import net.aeronetwork.core.database.mongo.dao.BannedPlayerDAO;
import net.aeronetwork.core.database.mongo.dao.MutedPlayerDAO;
import net.aeronetwork.core.database.mongo.dao.impl.BannedPlayerDAOImpl;
import net.aeronetwork.core.database.mongo.dao.impl.MutedPlayerDAOImpl;
import net.aeronetwork.core.event.player.ChatEvent;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.data.PunishmentData;
import net.aeronetwork.core.player.punishment.menu.MainPunishMenu;
import net.aeronetwork.core.player.punishment.player.BannedPlayer;
import net.aeronetwork.core.player.punishment.player.MutedPlayer;
import net.aeronetwork.core.redis.impl.PlayerMessageListener;
import net.aeronetwork.core.redis.impl.StaffMessageListener;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.TimeConverter;
import net.aeronetwork.core.util.UpdateType;
import net.aeronetwork.proxy.redis.listener.DisconnectListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class PunishmentManager implements Listener {

    private MorphiaService bannedService;
    private BannedPlayerDAO bannedPlayerDAO;

    private MorphiaService mutedService;
    private MutedPlayerDAO mutedPlayerDAO;

    private Cache<UUID, PunishmentData> punishmentDataCache;

    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss"); // Date format

    public PunishmentManager() {
        this.bannedService = new MorphiaService("aero_network", BannedPlayer.class);
        this.bannedPlayerDAO = new BannedPlayerDAOImpl(BannedPlayer.class, bannedService.getDatastore());

        this.mutedService = new MorphiaService("aero_network", MutedPlayer.class);
        this.mutedPlayerDAO = new MutedPlayerDAOImpl(MutedPlayer.class, mutedService.getDatastore());

        this.punishmentDataCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        Bukkit.getPluginManager().registerEvents(this, AeroCore.INSTANCE);
    }

    public MutedPlayer getMutedPlayer(UUID uuid) {
        return mutedPlayerDAO.getByUuid(uuid);
    }

    public BannedPlayer getBannedPlayer(UUID uuid) {
        return bannedPlayerDAO.getByUuid(uuid);
    }

    public boolean isMuted(UUID uuid) {
        return mutedPlayerDAO.getByUuid(uuid) != null;
    }

    public boolean isBanned(UUID uuid) {
        return bannedPlayerDAO.getByUuid(uuid) != null;
    }

    public boolean isPunished(UUID uuid) {
        return isMuted(uuid) || isBanned(uuid);
    }

    public PunishmentData refreshAndGet(UUID uuid) {
        MutedPlayer mutedPlayer = getMutedPlayer(uuid);
        if(mutedPlayer != null) {
            if(System.currentTimeMillis() > mutedPlayer.getEndTime()) {
                mutedPlayerDAO.delete(mutedPlayer);
                mutedPlayer = null;
            }
        }

        BannedPlayer bannedPlayer = getBannedPlayer(uuid);
        if(bannedPlayer != null) {
            if(System.currentTimeMillis() > bannedPlayer.getEndTime()) {
                bannedPlayerDAO.delete(bannedPlayer);
                bannedPlayer = null;
            }
        }

        PunishmentData data = new PunishmentData(mutedPlayer, bannedPlayer);
        punishmentDataCache.invalidate(uuid);
        punishmentDataCache.put(uuid, data);
        return data;
    }

    public PunishmentData getPunishmentData(UUID uuid) {
        try {
            return punishmentDataCache.get(uuid, () -> refreshAndGet(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if(event.getPlayer().getSessionObjects().containsKey(event.getPlayer().getUuid().toString())) {
            event.setCancelled(true);
            if(TimeConverter.isTimeString(event.getMessage())) {
                MainPunishMenu menu = (MainPunishMenu) event.getPlayer().getSessionObjects().get(event.getPlayer().getUuid().toString());
                long length = TimeConverter.convert(event.getMessage());
                punish(AeroCore.PLAYER_MANAGER.getPlayer(menu.getPlayer()),
                        new Punishment(event.getPlayer().getUuid(), length == -1 ? menu.getPunishmentType() : menu.getPunishmentType().getSubType().equals(Punishment.PunishmentSubType.BAN) ? Punishment.PunishmentType.TEMPORARY_BAN : Punishment.PunishmentType.TEMPORARY_MUTE, menu.getReason(), System.currentTimeMillis(), length, true));                event.getPlayer().getSessionObjects().remove(event.getPlayer().getUuid().toString());
            } else {
                event.getPlayer().sendMessage(FM.mainFormat("Punish", "Please provide a valid time period."));
            }
        }
    }

    /**
     * Punishes a player.
     *
     * @param player The player to punish.
     * @param punishment The punishment data.
     */
    public void punish(AeroPlayer player, Punishment punishment) {
        Executors.newSingleThreadExecutor().submit(() -> {
            punishment.setPunishmentId(UUID.randomUUID().toString());
            player.updatePunishment(punishment, UpdateType.ADD);
            if(punishment.getType().getSubType() == Punishment.PunishmentSubType.BAN) {
                BannedPlayer bannedPlayer = new BannedPlayer(
                        player.getUuid(),
                        player.getAccountName(),
                        punishment.getIssuer(),
                        punishment.getReason(),
                        punishment.getPunishedOn(),
                        punishment.isPermanent() ? -1 : punishment.getLength(),
                        punishment.isAppealAllowed()
                );
                bannedPlayerDAO.save(bannedPlayer);

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        StaffMessageListener.CHANNEL,
                        FM.mainFormat("Punish", "§c" +
                                AeroCore.PLAYER_MANAGER.getPlayer(punishment.getIssuer()).getAccountName() +
                                " §ebanned §c" + player.getAccountName() + " §efor §c"
                                + TimeConverter.convertToReadableTime(punishment.getLength())
                                + "§e. Reason: §c" + punishment.getReason())
                ));

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        DisconnectListener.CHANNEL,
                        player.getUuid().toString() + " §cYou have been banned!"
                ));
            } else if(punishment.getType().getSubType() == Punishment.PunishmentSubType.MUTE) {
                MutedPlayer mutedPlayer = new MutedPlayer(
                        player.getUuid(),
                        player.getAccountName(),
                        punishment.getIssuer(),
                        punishment.getReason(),
                        punishment.getPunishedOn(),
                        punishment.isPermanent() ? -1 : punishment.getLength(),
                        punishment.isAppealAllowed()
                );
                mutedPlayerDAO.save(mutedPlayer);

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        StaffMessageListener.CHANNEL,
                        FM.mainFormat("Punish", "§c" +
                                AeroCore.PLAYER_MANAGER.getPlayer(punishment.getIssuer()).getAccountName() +
                                " §emuted §c" + player.getAccountName() + " §efor §c"
                                + TimeConverter.convertToReadableTime(punishment.getLength())
                                + "§e. Reason: §c" + punishment.getReason())
                ));

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        PlayerMessageListener.CHANNEL,
                        player.getUuid().toString() + " " + FM.mainFormat("Punish",
                                "You have been muted for &c" + TimeConverter.convertToReadableTime(punishment.getLength())
                                        + "&e. Reason: &c" + punishment.getReason())
                ));
            } else if(punishment.getType().getSubType() == Punishment.PunishmentSubType.KICK) {
                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        StaffMessageListener.CHANNEL,
                        FM.mainFormat("Punish", "§c" +
                                AeroCore.PLAYER_MANAGER.getPlayer(punishment.getIssuer()).getAccountName() +
                                " §ekicked §c" + player.getAccountName() + " §efor §c"
                                + punishment.getReason())
                ));

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        DisconnectListener.CHANNEL,
                        player.getUuid().toString() + " §c" + punishment.getReason()
                ));
            } else if(punishment.getType().getSubType() == Punishment.PunishmentSubType.WARNING) {
                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        StaffMessageListener.CHANNEL,
                        FM.mainFormat("Punish", "§c" +
                                AeroCore.PLAYER_MANAGER.getPlayer(punishment.getIssuer()).getAccountName() +
                                " §ewarned §c" + player.getAccountName() + " §efor §c"
                                + punishment.getReason())
                ));

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                        PlayerMessageListener.CHANNEL,
                        player.getUuid().toString() + " " + FM.mainFormat("Punish", "You have been warned for &c"
                                + punishment.getReason() + "&e. Further warnings may lead to a harsher punishment.")
                ));
            }
            refreshAndGet(player.getUuid());
        });
    }

    /**
     * Removes a player's ban or mute data from the database.
     *
     * @param player The player to revoke a punishment from.
     * @param type The type of punishment to revoke.
     * @return The result of whether they were unpunished or not.
     */
    public CompletableFuture<Boolean> unpunish(AeroPlayer player, Punishment.PunishmentSubType type) {
        CompletableFuture<Boolean> executed = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            PunishmentData data = refreshAndGet(player.getUuid());
            if(type == Punishment.PunishmentSubType.BAN && data.isBanned()) {
                bannedPlayerDAO.delete(data.getBanData());
                executed.complete(true);
                getPunishmentDataCache().invalidate(player.getUuid());
            } else if(type == Punishment.PunishmentSubType.MUTE && data.isMuted()) {
                mutedPlayerDAO.delete(data.getMuteData());
                executed.complete(true);
                getPunishmentDataCache().invalidate(player.getUuid());
            }
            executed.complete(false);
        });
        return executed;
    }
}
