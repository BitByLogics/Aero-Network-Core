package net.aeronetwork.core.util.nms;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketUtil {

    /**
     * Set a player's player list.
     *
     * @param user - Player whose tab is being set.
     * @param header - Tab header.
     * @param footer - Tab footer.
     */
    public static void setPlayerList(Player user, String header, String footer) {
        PlayerConnection playerConnection = ((CraftPlayer) user).getHandle().playerConnection;
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, new ChatComponentText(ChatColor.translateAlternateColorCodes('&', header)));

            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, new ChatComponentText(ChatColor.translateAlternateColorCodes('&', footer)));

            headerField.setAccessible(false);
            footerField.setAccessible(false);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        playerConnection.sendPacket(packet);
    }

    /**
     * Send a title message to the player.
     *
     * @param user - Player whose receiving the message.
     * @param title - Title message.
     * @param subtitle - SubTitle message.
     * @param fadeIn - Fade in time (in ticks).
     * @param seen - Time the title will be seen (in ticks).
     * @param fadeOut - Fade out time (in ticks).
     */
    public static void sendTitle(Player user, String title, String subtitle, int fadeIn, int seen, int fadeOut) {
        PlayerConnection playerConnection = ((CraftPlayer) user).getHandle().playerConnection;
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(ChatColor.translateAlternateColorCodes('&', title)));
        PacketPlayOutTitle lengthPacket = new PacketPlayOutTitle(fadeIn, seen, fadeOut);
        PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(ChatColor.translateAlternateColorCodes('&', subtitle)));

        playerConnection.sendPacket(titlePacket);
        playerConnection.sendPacket(subTitlePacket);
        playerConnection.sendPacket(lengthPacket);
    }

    /**
     * Send a player an action bar message.
     *
     * @param user - Player whose receiving the message.
     * @param message - Message being displayed.
     */
    public static void sendActionBar(Player user, String message) {
        PlayerConnection playerConnection = ((CraftPlayer) user).getHandle().playerConnection;

        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)), (byte) 2);

        playerConnection.sendPacket(packetPlayOutChat);
    }

}
