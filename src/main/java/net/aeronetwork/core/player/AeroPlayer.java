package net.aeronetwork.core.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.player.disguise.DisguiseData;
import net.aeronetwork.core.player.punishment.Punishment;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.UpdateType;
import net.aeronetwork.core.util.message.translate.language.Language;
import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity(value = "aero_players", noClassnameStored = true)
public class AeroPlayer implements UndefinedPlayer {

    @Id
    private UUID uuid;
    private String accountName;

    private List<String> ranks = Lists.newCopyOnWriteArrayList();

    private List<String> ips = Lists.newCopyOnWriteArrayList();
    private List<Punishment> punishments = Lists.newCopyOnWriteArrayList();

    private long firstLogin = System.currentTimeMillis();
    private long lastLogin;
    private long playTime = 0;

    private String language = Language.ENGLISH_US.name();

    private double multiplier = 1.0;

    private long coins = 0;
    private long crystals = 0;
    private long experience = 0;

    private String discordId;

    // Disguise details
    private boolean disguised = false;
    private DisguiseData disguiseData;

    // Staff Options
    private boolean vanished = false;
    private boolean inStaffMode = false;

    // Session tracking
    private transient long sessionTime = System.currentTimeMillis();
    @Setter(AccessLevel.NONE)
    private transient Map<String, Object> sessionObjects = Maps.newConcurrentMap();

    @Deprecated
    public AeroPlayer() {
    }

    public AeroPlayer(UUID uuid) {
        this.uuid = uuid;
        this.accountName = "";
    }

    public AeroPlayer(UUID uuid, String accountName) {
        this.uuid = uuid;
        this.accountName = accountName;
    }

    public Rank getRank() {
        Rank highestRank = Rank.getHighestRank(getRanks().toArray(new Rank[0]));
        if(highestRank == null) {
            highestRank = Rank.DEFAULT;
            addRank(Rank.DEFAULT);
        }

        return highestRank;
    }

    public List<Rank> getRanks() {
        List<Rank> ranks = this.ranks.stream()
                .map(Rank::match)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(ranks.isEmpty())
            ranks.add(Rank.DEFAULT);
        return ranks;
    }

    public boolean addRank(Rank rank) {
        if(!hasRank(rank)) {
            ranks.add(rank.name());
            AeroCore.PLAYER_MANAGER.updateField(this, "ranks", ranks);
            return true;
        }
        return false;
    }

    public boolean revokeRank(Rank rank) {
        boolean removed = ranks.remove(rank.name());
        if(removed)
            AeroCore.PLAYER_MANAGER.updateField(this, "ranks", ranks);
        return removed;
    }

    public boolean hasRank(String rank) {
        return ranks.stream()
                .filter(r -> r.equalsIgnoreCase(rank))
                .findFirst()
                .orElse(null) != null;
    }

    public boolean hasRank(Rank rank) {
        return hasRank(rank.name());
    }

    public boolean isDonator() {
        return false;
    }

    public boolean isStaff() {
        return false;
    }

    public boolean isHighStaff() {
        return false;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
        AeroCore.PLAYER_MANAGER.updateField(this, "accountName", this.accountName);
    }

    public List<Punishment> getPunishments() {
        List<Punishment> cloned = Lists.newArrayList();
        this.punishments.forEach(punishment -> cloned.add(punishment.clone()));

        return cloned;
    }

    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment);
    }

    public Language getLanguage() {
        return Language.match(this.language);
    }

    public void updatePunishment(Punishment punishment, UpdateType type) {
        if(type == UpdateType.ADD) {
            this.punishments.add(punishment);
        } else if(type == UpdateType.REMOVE) {
            CompletableFuture<Punishment> toRemove = new CompletableFuture<>();
            this.punishments.forEach(p -> {
                if(p.getPunishmentId() == null)
                   p.setPunishmentId(UUID.randomUUID().toString());
                if(p.getPunishmentId().equals(punishment.getPunishmentId()))
                    toRemove.complete(p);
            });

            Punishment p = toRemove.getNow(null);
            if(p != null) {
                this.punishments.remove(p);
                if(p.isValid())
                    AeroCore.PUNISHMENT_MANAGER.unpunish(this, p.getType().getSubType());
            }
        } else {
            this.punishments.forEach(p -> {
                if(p.getPunishmentId() != null && p.getPunishmentId().equals(punishment.getPunishmentId())) {
                    if(p.isValid() && !punishment.isValid()) {
                        AeroCore.PUNISHMENT_MANAGER.unpunish(this, p.getType().getSubType());
                    }
                    p.copy(punishment);
                }
            });
        }
        updatePunishments();
    }

    public void updateIPList() {
        AeroCore.PLAYER_MANAGER.updateField(this, "ips", this.ips);
    }

    public void updateFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
        AeroCore.PLAYER_MANAGER.updateField(this, "firstLogin", this.firstLogin);
    }

    public void updateLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
        AeroCore.PLAYER_MANAGER.updateField(this, "lastLogin", this.lastLogin);
    }

    public void updatePlayTime(long playTime) {
        this.playTime = playTime;
        AeroCore.PLAYER_MANAGER.updateField(this, "playTime", this.playTime);
    }

    public void updateCoins(long coins) {
        this.coins = coins;
        AeroCore.PLAYER_MANAGER.updateField(this, "coins", this.coins);
    }

    public void updateCrystals(long crystals) {
        this.crystals = crystals;
        AeroCore.PLAYER_MANAGER.updateField(this, "crystals", this.crystals);
    }

    public void updateExperience(long experience) {
        this.experience = experience;
        AeroCore.PLAYER_MANAGER.updateField(this, "experience", this.experience);
    }

    public void updateDiscordId(String discordId) {
        this.discordId = discordId;
        AeroCore.PLAYER_MANAGER.updateField(this, "discordId", this.discordId);
    }

    public void updateDisguised(boolean disguised) {
        this.disguised = disguised;
        AeroCore.PLAYER_MANAGER.updateField(this, "disguised", this.disguised);
    }

    public void updateDisguiseData(DisguiseData disguiseData) {
        this.disguiseData = disguiseData;
        AeroCore.PLAYER_MANAGER.updateField(this, "disguiseData", this.disguiseData);
    }

    public void updateVanished(boolean vanished) {
        this.vanished = vanished;
        AeroCore.PLAYER_MANAGER.updateField(this, "vanished", this.vanished);
    }

    public void updateStaffMode(boolean staffMode) {
        this.inStaffMode = staffMode;
        AeroCore.PLAYER_MANAGER.updateField(this, "inStaffMode", this.inStaffMode);
    }

    public void updatePunishments() {
        AeroCore.PLAYER_MANAGER.updateField(this, "punishments", this.punishments);
    }

    public void addSessionObject(String key, Object object) {
        sessionObjects.put(key, object);
    }

    public void sendMessage(String message) {
        AeroCore.INSTANCE.getServer().getPlayer(uuid).sendMessage(message);
    }

    public Player getBukkitPlayer() {
        return AeroCore.INSTANCE.getServer().getPlayer(uuid);
    }
}
