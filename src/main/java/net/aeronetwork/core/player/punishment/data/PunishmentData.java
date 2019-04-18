package net.aeronetwork.core.player.punishment.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.aeronetwork.core.player.punishment.player.BannedPlayer;
import net.aeronetwork.core.player.punishment.player.MutedPlayer;

@Data
@AllArgsConstructor
public class PunishmentData {

    private MutedPlayer muteData;
    private BannedPlayer banData;

    public boolean isMuted() {
        return muteData != null;
    }

    public boolean isBanned() {
        return banData != null;
    }
}
