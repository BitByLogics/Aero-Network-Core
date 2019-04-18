package net.aeronetwork.core.cosmetic;

import org.bukkit.inventory.ItemStack;

/**
 * Defines the variables used to identify a category for cosmetics. This
 * interface should only be used with an {@link Enum}.
 */
public interface CosmeticType {

    /**
     * Gets the friendly name of the type.
     *
     * @return The friendly name of the type.
     */
    String getFriendlyName();

    /**
     * Gets the {@link ItemStack} used for the icon.
     *
     * @return The icon of the type.
     */
    ItemStack getIcon();
}
