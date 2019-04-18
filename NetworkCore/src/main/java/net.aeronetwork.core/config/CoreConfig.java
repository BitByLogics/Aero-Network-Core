package net.aeronetwork.core.config;

import lombok.Getter;

/**
 * Defines a data class to be used to parse JSON files with
 * {@link com.google.gson.Gson}.
 */
@Getter
public class CoreConfig {

    private String ip = "127.0.0.1";
    private int portRangeMin = 27000;
    private int portRangeMax = 30000;
}
