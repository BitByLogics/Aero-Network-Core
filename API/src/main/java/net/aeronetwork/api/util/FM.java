package net.aeronetwork.api.util;

import org.bukkit.ChatColor;

public class FM {

    //§

    /**
     * Default server format message.
     *
     * @param body Body of the message.
     * @return The message with the format.
     */
    public static String mainFormat(String body) {
        return "§4§lAero Network §8» §e" + ChatColor.translateAlternateColorCodes('&', body);
    }

    /**
     * Format with custom module name.
     *
     * @param module Module name.
     * @param body Body of the message.
     * @return The message with the format.
     */
    public static String mainFormat(String module, String body) {
        return "§4§l" + module + " §8» §e" + ChatColor.translateAlternateColorCodes('&', body);
    }

    /**
     * Format with a command.
     *
     * @param command The command without a slash.
     * @param description The description of the command.
     * @return A formatted command message.
     */
    public static String command(String command, String description) {
        return "§c/" + command + " §8- §e" + description;
    }

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
