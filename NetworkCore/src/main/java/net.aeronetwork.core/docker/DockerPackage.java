package net.aeronetwork.core.docker;

import lombok.Data;
import net.aeronetwork.core.server.ServerType;

import java.util.List;

@Data
public class DockerPackage {

    private List<String> ids;
    private int cache;
    private int portRangeMin;
    private int portRangeMax;
    private String root;
    private List<String> envVars;
    private ServerType serverType;

    private List<String> includedPlugins;
    private String world;

    private transient String instance;
    private transient String holdingJar;

    public DockerPackage() {
    }

    public DockerPackage(List<String> ids, int cache, int portRangeMin, int portRangeMax, String root,
                         List<String> envVars, ServerType serverType, List<String> includedPlugins,
                         String world) {
        this.ids = ids;
        this.cache = cache;
        this.portRangeMin = portRangeMin;
        this.portRangeMax = portRangeMax;
        this.root = root;
        this.envVars = envVars;
        this.serverType = serverType;
        this.includedPlugins = includedPlugins;
        this.world = world;
    }

    public boolean isPortRangeValid() {
        return !(portRangeMin == -1 || portRangeMax == -1);
    }
}
