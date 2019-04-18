package net.aeronetwork.core.server;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class AeroServer {

    private String aeroId;
    private String instance;
    private List<String> ids;
    private ServerType serverType;

    private String ip;
    private int boundPort;

    private int maxPlayers;
    private JoinState joinState;
    private boolean privateServer;

    private List<UUID> players = Lists.newArrayList();

    public AeroServer() {
    }

    public enum JoinState {
        JOINABLE,
        NOT_JOINABLE
    }
}
