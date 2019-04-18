package net.aeronetwork.core.server;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Defines the type of a deployable server.
 */
@AllArgsConstructor
@Getter
public enum ServerType {

    SERVER_LOBBY(
            "spigot.jar",
            Lists.newArrayList("server.properties", "spigot.yml"),
            "-Xms512m -Xmx1024m -Dcom.mojang.eula.agree=true",
            "--world world",
            Lists.newArrayList("AeroCore", "AAC", "ConditionalCommands",
                    "ProtocolLib", "ViaVersion", "WorldEdit", "NametagEdit"),
            Lists.newArrayList("AAC", "NametagEdit")
    ),
    SERVER_GAME(
            "spigot.jar",
            Lists.newArrayList("server.properties", "spigot.yml"),
            "-Xms512m -Xmx1024m -Dcom.mojang.eula.agree=true",
            "--world world",
            Lists.newArrayList("AeroCore", "Enflow", "AAC", "ConditionalCommands",
                    "ProtocolLib", "ViaVersion", "WorldEdit", "NametagEdit"),
            Lists.newArrayList("AAC", "NametagEdit")
    ),
    SESSION_PROXY(
            "bungeecord.jar",
            Lists.newArrayList("config.yml"),
            "-Xms512m -Xmx1024m",
            null,
            Lists.newArrayList("AeroCore"),
            null
    ),
    TRANSPORT_PROXY(
            "bungeecord.jar",
            Lists.newArrayList("config.yml"),
            "-Xms512m -Xmx1024m",
            null,
            Lists.newArrayList("AeroCore"),
            null
    );

    private String jarName;
    private List<String> configFiles;
    private String commandLineArguments;
    private String postCommandLineArguments;
    private List<String> requiredPlugins;
    private List<String> requiredConfigs;
}
