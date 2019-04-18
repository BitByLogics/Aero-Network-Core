package net.aeronetwork.core.cosmetic;

import org.bukkit.entity.Player;

/**
 * Defines a cosmetic that can be run.
 */
public interface RunnableCosmetic {

    /**
     * Gets the period for {@link RunnableCosmetic#onTick(Player)} to be
     * called.
     *
     * @return The period in ticks. One second is 20 ticks.
     */
    long getTickingPeriodTicks();

    /**
     * Called when a cosmetic is selected.
     *
     * @param player The player that selected the cosmetic.
     */
    void onSelect(Player player);

    /**
     * Called when the cosmetic is ticked.
     *
     * @param player The player that has the cosmetic selected.
     */
    void onTick(Player player);

    /**
     * Called when a cosmetic is unselected.
     *
     * @param player The player that unselected the cosmetic.
     */
    void onUnselect(Player player);
}
