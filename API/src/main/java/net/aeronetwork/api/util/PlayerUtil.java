package net.aeronetwork.api.util;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerUtil {

    /**
     * Clear the player's inventory.
     *
     * @param p Player whose inventory is being cleared.
     */
    public static void clearInventory(Player p) {
        p.getInventory().clear();
    }

    /**
     * Clear all action potion effects on the player.
     *
     * @param p Player whose active effects are being cleared.
     */
    public static void clearEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }

    /**
     * Reset the player's health. (Resets max health to 20)
     *
     * @param p Player whose health is being reset.
     */
    public static void resetHealth(Player p) {
        p.setMaxHealth(20);
        p.setHealth(20);
    }

    /**
     * Clear the player's armor.
     *
     * @param p Player whose armor is being cleared.
     */
    public static void clearArmor(Player p) {
        p.getInventory().setArmorContents(new ItemStack[] { new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                new ItemStack(Material.AIR), new ItemStack(Material.AIR) });
    }

    /**
     * Reset a player's time.
     *
     * @param p Player whose time is being reset.
     */
    public static void resetTime(Player p) {
        p.resetPlayerTime();
    }

    /**
     * Reset a player's weather.
     *
     * @param p Player whose weather is being reset.
     */
    public static void resetWeather(Player p) {
        p.resetPlayerWeather();
    }

    /**
     * Send packets to a player.
     *
     * @param p Player who is receiving the packets.
     * @param packet The packets being sent to the player.
     */
    public static void sendPacket(Player p, Packet<?>... packet) {
        CraftPlayer cp = (CraftPlayer) p;
        for (Packet<?> pac : packet) {
            cp.getHandle().playerConnection.sendPacket(pac);
        }
    }

    /**
     * Send a player a title message.
     *
     * @param p Player who is receiving the title message.
     * @param title The title message.
     * @param subtitle The subtitle message.
     * @param time The time they will see the title (in ticks).
     */
    public static void title(Player p, String title, String subtitle, int time) {
        PacketUtil.sendTitle(p, title, subtitle, 5, time, 5);
    }

    /**
     * Send a player an action bar message.
     *
     * @param p Player whose receiving the message.
     * @param message The message being displayed.
     */
    public static void actionBar(Player p, String message) {
        PacketUtil.sendActionBar(p, message);
    }

    /**
     * Heal a player back to their max health.
     *
     * @param p Player whose health is being restored.
     */
    public static void heal(Player p) {
        p.setHealth(p.getMaxHealth());
    }

    /**
     * Reset a player's food and saturation.
     *
     * @param p Player whose food and saturation is being restored.
     */
    public static void feed(Player p) {
        p.setFoodLevel(20);
        p.setSaturation(20);
    }

    /**
     * Reset the arrows displayed in the player.
     *
     * (MAY BREAK DUE TO VERSION CHANGE)
     *
     * @param p Player whose arrows are being reset.
     */
    public static void resetArrows(Player p) {
        getHandle(p).getDataWatcher().watch(9, (byte) 0);
    }

    /**
     * Kick a player from the server.
     *
     * @param p Player whose being kicked.
     * @param reason Reason the player is being kicked.
     */
    public static void kick(Player p, String reason) {
        p.kickPlayer(reason);
    }

    /**
     * Disconnect a player from the server.
     *
     * @param p Player whose being disconnected.
     * @param reason Reason the player is being disconnected.
     */
    public static void disconnect(Player p, String reason) {
        CraftPlayer cp = (CraftPlayer) p;
        cp.disconnect(reason);
    }

    /**
     * Clear the player's fire ticks.
     *
     * @param p Player whose fire ticks are being reset.
     */
    public static void clearFire(Player p) {
        p.setFireTicks(0);
    }

    /**
     * Get a EntityPlayer instance of the player.
     *
     * @param p Player whose EntityPlayer instance is being retrieved.
     * @return EntityPlayer Player's EntityPlayer instance.
     */
    public static EntityPlayer getHandle(Player p) {
        CraftPlayer cp = (CraftPlayer) p;
        return cp.getHandle();
    }

    /**
     * Reset a player's experience.
     *
     * @param p Player whose experience is being reset.
     */
    public static void resetExperience(Player p) {
        p.setTotalExperience(0);
    }

    /**
     * Reset everything about the player.
     *
     * @param p Player whose being reset.
     */
    public static void reset(Player p) {
        clearInventory(p);
        clearArmor(p);
        clearEffects(p);
        clearFire(p);
        feed(p);
        resetHealth(p);
        resetTime(p);
        resetWeather(p);
        resetExperience(p);
        resetArrows(p);
        clearFire(p);
        p.setMaximumNoDamageTicks(20);
    }

}