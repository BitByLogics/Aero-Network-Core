package net.aeronetwork.core.server;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Defines an AeroServer that stores all relevant information about
 * any deployed server.
 */
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
    private JoinState joinState = JoinState.JOINABLE;
    private boolean privateServer;

    private List<UUID> players = Lists.newArrayList();

    /**
     * Constructs a new instance with the default setting and info values.
     */
    public AeroServer() {
    }

    /**
     * Used to indicate whether a server is accepting players.
     */
    public enum JoinState {
        JOINABLE,
        NOT_JOINABLE
    }
}
