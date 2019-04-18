package net.aeronetwork.core.player.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Punishment implements Cloneable {

    private UUID issuer;
    private PunishmentType type;
    private String reason;
    private long punishedOn;
    private long length;
    private boolean appealAllowed;

    @Setter
    private String punishmentId;
    @Setter
    private boolean forceExpired;

    @Deprecated
    public Punishment() {
    }

    public Punishment(UUID issuer, PunishmentType type, String reason, long punishedOn, long length, boolean appealAllowed) {
        this.issuer = issuer;
        this.type = type;
        this.reason = reason;
        this.punishedOn = punishedOn;
        this.length = length;
        this.appealAllowed = appealAllowed;
    }

    public boolean isPermanent() {
        return type.isPermanent();
    }

    public boolean isValid() {
        return !forceExpired && (isPermanent() || System.currentTimeMillis() <= (punishedOn + length));
    }

    public void copy(Punishment punishment) {
        this.issuer = punishment.getIssuer();
        this.type = punishment.getType();
        this.reason = punishment.getReason();
        this.punishedOn = punishment.getPunishedOn();
        this.length = punishment.getLength();
        this.appealAllowed = punishment.isAppealAllowed();

        this.punishmentId = punishment.getPunishmentId();
        this.forceExpired = punishment.isForceExpired();
    }

    @Override
    public Punishment clone() {
        try {
            return (Punishment) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AllArgsConstructor
    @Getter
    public enum PunishmentType {

        WARNING(PunishmentSubType.WARNING, false),
        KICK(PunishmentSubType.KICK, false),
        TEMPORARY_MUTE(PunishmentSubType.MUTE, false),
        PERMANENT_MUTE(PunishmentSubType.MUTE, true),
        TEMPORARY_BAN(PunishmentSubType.BAN, false),
        PERMANENT_BAN(PunishmentSubType.BAN, true);

        private PunishmentSubType subType;
        private boolean permanent;
    }

    public enum PunishmentSubType {

        WARNING,
        KICK,
        MUTE,
        BAN
    }
}
