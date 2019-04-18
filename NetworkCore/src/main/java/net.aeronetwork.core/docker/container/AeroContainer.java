package net.aeronetwork.core.docker.container;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AeroContainer {

    private String dockerId;
    private String aeroId;
    private int boundPort;
}
