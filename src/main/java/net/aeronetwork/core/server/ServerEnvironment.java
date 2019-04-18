package net.aeronetwork.core.server;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

/**
 * Server environment for Aero. Retrieves and stores all Aero-related
 * environment variables.
 */
@Getter
public class ServerEnvironment {

    private Map<String, String> envVariables;

    public ServerEnvironment() {
        this.envVariables = Maps.newConcurrentMap();
        setup();
    }

    /**
     * Sets up the environment to retrieve all Aero related variables.
     */
    private void setup() {
        Arrays.stream(EnvType.values()).forEach(env -> {
            String variable = System.getenv(env.name());
            envVariables.put(env.name(), variable != null ? variable : env.getDefaultValue());
        });
    }

    /**
     * Gets an environment variable.
     *
     * @param var The variable to get.
     * @param defaultVal The value to default to if {@link System#getenv()} returns
     *                   null.
     * @return The value set for the environment variable, or defaultVal if null.
     */
    public String getEnv(String var, String defaultVal) {
        String val = envVariables.getOrDefault(var, defaultVal);
        if(val == null)
            setEnv(var, defaultVal);

        return val != null ? val : defaultVal;
    }

    /**
     * Gets an environment variable by {@link EnvType}.
     *
     * @param type The type of environment variable.
     * @return The value of the environment variable, or "none" (in {@link String}
     * form) if the variable doesn't exist.
     */
    public String getEnv(EnvType type) {
        return getEnv(type.name(), "none");
    }

    /**
     * Sets an environment variable with the returned value from {@link System#getenv()},
     * or defaults to defaultVal.
     *
     * @param var The variable to set.
     * @param defaultVal The value to default to if {@link System#getenv()} returns null.
     *
     * @deprecated
     */
    @Deprecated
    public void setEnv(String var, String defaultVal) {
        String val = System.getenv(var);
        envVariables.put(var, val != null ? val : defaultVal);
    }

    @AllArgsConstructor
    @Getter
    public enum EnvType {

        AERO_ID("none"),
        AERO_IP("none"),
        AERO_BOUND_PORT("25565"),
        AERO_INSTANCE_NAME("n/a"),
        AERO_PRIVATE_SERVER("false");

        private String defaultValue;
    }
}
