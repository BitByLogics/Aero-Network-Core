package net.aeronetwork.core.cosmetic;

import net.aeronetwork.core.player.rank.Rank;

/**
 * Defines a cosmetic requiring a {@link Rank} to be used.
 */
public interface RankCosmetic {

    /**
     * The minimum rank required for any action to be invoked on the
     * cosmetic.
     *
     * @return The minimum {@link Rank} required.
     */
    Rank getMinRank();

    /**
     * Whether the cosmetic is available as a default cosmetic for the
     * {@link Rank}.
     *
     * @return TRUE if the rank cosmetic is default for all users with
     * rank or higher, or FALSE if the cosmetic requires purchasing or
     * some other requirement.
     */
    boolean isDefaultRankCosmetic();
}
