package net.aeronetwork.core.cosmetic;

import org.bukkit.inventory.ItemStack;

/**
 * Defines the base for all cosmetics. All valid cosmetics must implement
 * this interface. Cosmetics may also choose to implement other modules
 * along side this interface.
 *
 * @see MultiSelectableCosmetic
 * @see NonPurchasableCosmetic
 * @see RankCosmetic
 * @see RunnableCosmetic
 */
public interface Cosmetic {

    /**
     * Gets the unique ID of the cosmetic. Duplicate IDs can result in
     * issues.
     *
     * @return The unique ID for this cosmetic.
     */
    String getId();

    /**
     * Gets the friendly and displayable name of the cosmetic.
     *
     * @return The friendly name for the cosmetic.
     */
    String getName();

    /**
     * Gets the description of the cosmetic.
     *
     * @return The description of the cosmetic.
     */
    String getDescription();

    /**
     * Gets the cost of the cosmetic.
     *
     * @return The cost of the cosmetic.
     */
    long getCost();

    /**
     * Gets the enum type of the cosmetic. This value is used to categorize
     * cosmetics.
     *
     * @return The enum type of the cosmetic.
     * @see CosmeticType
     */
    Enum<? extends CosmeticType> getType();

    /**
     * Gets the icon to display for the cosmetic.
     *
     * @return The icon of the cosmetic.
     */
    ItemStack getIcon();
}
