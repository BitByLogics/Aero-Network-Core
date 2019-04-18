package net.aeronetwork.core.server.settings;

import lombok.Data;
import net.aeronetwork.core.server.AeroServer;

@Data
public class ServerSettings {

    private boolean privateMode;
    private boolean disableStatTracking;
    private boolean offlineMode;

    private int maxPlayers;
    private AeroServer.JoinState joinState;
    private String motd;

    public ServerSettings() {
        this.privateMode = false;
        this.disableStatTracking = false;
        this.offlineMode = false;

        this.maxPlayers = 500;
        this.joinState = AeroServer.JoinState.JOINABLE;
        this.motd = null;
    }
}
