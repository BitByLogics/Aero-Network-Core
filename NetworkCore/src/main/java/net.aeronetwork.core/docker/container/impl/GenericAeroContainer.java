package net.aeronetwork.core.docker.container.impl;

import net.aeronetwork.core.docker.container.AeroContainer;

public class GenericAeroContainer extends AeroContainer {

    public GenericAeroContainer(String dockerId, String aeroId, int boundPort) {
        super(dockerId, aeroId, boundPort);
    }
}
