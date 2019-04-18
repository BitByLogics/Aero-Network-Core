package net.aeronetwork.core.player.network;

import lombok.Data;

import java.util.UUID;

@Data
public class NetworkPlayer {

    private UUID uuid;
    private String name;
    private String server;

    public NetworkPlayer() {
    }

    public NetworkPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public NetworkPlayer(UUID uuid, String name, String server) {
        this.uuid = uuid;
        this.name = name;
        this.server = server;
    }

    public NetworkPlayer(UUID uuid, String server) {
        this.uuid = uuid;
        this.server = server;
    }
}
