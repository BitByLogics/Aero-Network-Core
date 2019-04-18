package net.aeronetwork.core.player.punishment.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.aeronetwork.core.player.UndefinedPlayer;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Entity(value = "aero_muted_players", noClassnameStored = true)
public class MutedPlayer implements UndefinedPlayer {

    @Id
    private UUID uuid;
    private String accountName;
    private UUID issuer;
    private String reason;
    private long bannedOn;
    private long length;
    private boolean appealAllowed;

    public MutedPlayer() {
    }

    public long getEndTime() {
        return bannedOn + length;
    }

    public boolean isPermanent() {
        return (length == -1);
    }
}
