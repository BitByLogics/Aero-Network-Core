package net.aeronetwork.core.config.util;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class ConfigUtil {

    /**
     * Copy the internal config instance from specified plugin.
     *
     * @param plugin The plugin where the config will be saved.
     * @param file The file to be copied and saved.
     * @param replace Replace the file if it already exists.
     */
    public static void saveDefaultConfig(JavaPlugin plugin, String file, boolean replace) {
        if (file == null || file.equals("")) {
            throw new NullPointerException("Couldn't locate config resource.");
        }

        file = file.replace('\\', '/');
        InputStream in = plugin.getResource(file);

        if (in == null) {
            return;
        }

        File outFile = new File(plugin.getDataFolder(), file);
        int lastIndex = file.lastIndexOf('/');
        File outDir = new File(plugin.getDataFolder(), file.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            System.out.println("[Aero Core] [ERROR HANDLER]: Couldn't save default config for '" + file + ".yml'.");
        }
    }

    public static void reloadConfig(JavaPlugin plugin, FileConfiguration config, File configFile) {
        config = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = plugin.getResource(configFile.getName());
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;

        final byte[] contents;
        defConfig = new YamlConfiguration();
        try {
            contents = ByteStreams.toByteArray(defConfigStream);
        } catch (final IOException e) {
            System.out.println("[Aero Core] [ERROR HANDLER]: Couldn't reload config for '" + configFile + ".yml'.");
            return;
        }

        final String text = new String(contents, Charset.defaultCharset());

        if (!text.equals(new String(contents, Charsets.UTF_8))) {
            System.out.println("[Aero Core] [ERROR HANDLER]: Couldn't reload config for '" + configFile + ".yml'.");
        }

        try {
            defConfig.loadFromString(text);
        } catch (final InvalidConfigurationException e) {
            System.out.println("[Aero Core] [ERROR HANDLER]: Couldn't reload config for '" + configFile + ".yml'.");
        }

        config.setDefaults(defConfig);
    }

}
