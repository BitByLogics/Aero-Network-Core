package net.aeronetwork.core.cosmetic;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.cosmetic.menu.CosmeticCategoryMenu;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.player.AeroPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Defines a manager used to manage cosmetic menus, selections, purchases,
 * etc.
 */
public abstract class CosmeticManager implements Listener {

    @Getter
    private List<Cosmetic> cosmetics;

    private Map<Cosmetic, TickData> cosmeticTicking;

    public CosmeticManager() {
        this.cosmetics = Lists.newCopyOnWriteArrayList();
        this.cosmeticTicking = Maps.newConcurrentMap();
    }

    /**
     * Gets a cosmetic by the specified ID.
     *
     * @param id The ID of the cosmetic.
     * @return The cosmetic with the associated ID, or null if none.
     */
    public Cosmetic getCosmeticById(String id) {
        return this.cosmetics.stream()
                .filter(cosmetic -> cosmetic.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a new {@link Cosmetic} to the cosmetic list. Added cosmetics will be
     * considered valid cosmetics.
     *
     * @param cosmetic The {@link Cosmetic} to add.
     */
    public void addCosmetic(Cosmetic cosmetic) {
        this.cosmetics.add(cosmetic);
        if(cosmetic instanceof RunnableCosmetic) {
            RunnableCosmetic rc = (RunnableCosmetic) cosmetic;
            this.cosmeticTicking.put(cosmetic, new TickData(AeroCore.INSTANCE, rc, rc.getTickingPeriodTicks()));
        }
    }

    /**
     * Adds a player to the specified cosmetic's {@link TickData}, signifying that
     * all actions related to this cosmetic can be invoked on this player.
     *
     * @param player The player to add.
     * @param cosmetic The cosmetic to add to. Cosmetic must implement {@link RunnableCosmetic}.
     */
    public void addPlayerToCosmeticTicking(Player player, Cosmetic cosmetic) {
        if(player != null)
            this.cosmeticTicking.forEach((c, data) -> {
                if(c.getId().equalsIgnoreCase(cosmetic.getId()) &&
                        !data.getPlayers().contains(player.getUniqueId())) {
                    data.getCosmetic().onSelect(player);
                    data.getPlayers().add(player.getUniqueId());
                }
            });
    }

    /**
     * Removes a player from the specified cosmetic's {@link TickData}, signifying that
     * all actions related to this cosmetic can no longer be invoked on this player.
     *
     * @param player The player to remove.
     * @param cosmetic The cosmetic to remove from. Cosmetic must implement{@link RunnableCosmetic}.
     */
    public void removePlayerFromCosmeticTicking(Player player, Cosmetic cosmetic) {
        if(player != null)
            this.cosmeticTicking.forEach((c, data) -> {
                if(c.getId().equalsIgnoreCase(cosmetic.getId())) {
                    data.getCosmetic().onUnselect(player);
                    data.getPlayers().remove(player.getUniqueId());
                }
            });
    }

    /**
     * Gets all unique types of all valid cosmetics.
     *
     * @return A list of enum constants signifying all the different categories.
     */
    public List<Enum<? extends CosmeticType>> getAllTypes() {
        return this.cosmetics.stream()
                .map(Cosmetic::getType)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Parses a {@link Map} keyed by a string, with boolean values to key by
     * {@link Cosmetic} and boolean values instead.
     *
     * String keys refer to the ID of the cosmetic, as specified in {@link Cosmetic#getId()},
     * while booleans refer to whether the cosmetic is selected.
     *
     * @param cosmeticIds The map keyed by string IDs.
     * @return The parsed map keyed by {@link Cosmetic}.
     */
    public Map<Cosmetic, Boolean> parse(Map<String, Boolean> cosmeticIds) {
        Map<Cosmetic, Boolean> parsed = Maps.newConcurrentMap();

        cosmeticIds.forEach((id, selected) -> {
            Cosmetic cosmetic = getCosmeticById(id);

            if(cosmetic != null)
                parsed.put(cosmetic, selected);
        });

        return parsed;
    }

    /**
     * Converts a cosmetic map keyed by {@link Cosmetic} with boolean values
     * to be keyed by the string ID instead. The returned map provides easier
     * to work with data to store in databases.
     *
     * @param cosmetics The map keyed by {@link Cosmetic}.
     * @return A map keyed by string IDs.
     */
    public Map<String, Boolean> convert(Map<Cosmetic, Boolean> cosmetics) {
        Map<String, Boolean> converted = Maps.newConcurrentMap();

        cosmetics.forEach((cosmetic, selected) ->
                converted.put(cosmetic.getId(), selected));

        return converted;
    }

    /**
     * Selects a specific cosmetic in the specified cosmetics.
     *
     * @param cosmetics The cosmetics used to select the specific cosmetic.
     * @param id The ID of the cosmetic to select.
     * @param forceSelect Whether to force select the cosmetic if it is not
     *                    purchased already.
     * @return The updated map with the selected cosmetic.
     */
    @Beta
    public Map<Cosmetic, Boolean> select(Map<Cosmetic, Boolean> cosmetics, String id, boolean forceSelect) {
        CompletableFuture<Cosmetic> toSelect = new CompletableFuture<>();
        cosmetics.forEach((cosmetic, selected) -> {
            if(cosmetic.getId().equals(id))
                toSelect.complete(cosmetic);
        });

        if(toSelect.getNow(null) != null) {
            Cosmetic c = toSelect.getNow(null);
            if(!(c instanceof MultiSelectableCosmetic)) {
                List<Cosmetic> selectedTypes = Lists.newArrayList();

                cosmetics.forEach((cosmetic, selected) -> {
                    if(cosmetic.getType() == c.getType() && !cosmetic.getId().equals(id)) {
                        if(!(cosmetic instanceof MultiSelectableCosmetic))
                            selectedTypes.add(cosmetic);
                    }
                });

                selectedTypes.forEach(cosmetic -> cosmetics.replace(cosmetic, false));
            }

            cosmetics.replace(c, true);
        }

        if(forceSelect) {
            Cosmetic c = getCosmeticById(id);
            if(c != null)
                cosmetics.put(c, true);
        }

        return cosmetics;
    }


    /**
     * Selects a cosmetic for a player. Unlike {@link CosmeticManager#select(Map, String, boolean)},
     * this method manages the ticking of the cosmetic for the player.
     *
     * @param player The player to select the cosmetic for.
     * @param cosmetic The {@link Cosmetic} to select.
     * @param forceSelect Whether to force select the cosmetic. Ticking will still be done,
     *                    regardless of this option.
     * @param update Whether to call {@link CosmeticManager#onUpdate(UUID, Map)}.
     */
    public void select(Player player, Cosmetic cosmetic, boolean forceSelect, boolean update) {
        Map<Cosmetic, Boolean> cosmetics = getCosmetics(player.getUniqueId());
        if(cosmetics != null) {
            select(cosmetics, cosmetic.getId(), forceSelect);

            if(cosmetic instanceof RunnableCosmetic) {
                addPlayerToCosmeticTicking(player, cosmetic);
            }

            cosmetics.forEach((c, selected) -> {
                if(c instanceof RunnableCosmetic && !selected) {
                    removePlayerFromCosmeticTicking(player, c);
                }
            });

            if(update)
                onUpdate(player.getUniqueId(), convert(cosmetics));
        }
    }

    /**
     * Selects a cosmetic for a player. Unlike {@link CosmeticManager#select(Map, String, boolean)},
     * this method manages the ticking of the cosmetic for the player.
     *
     * This method will not call {@link CosmeticManager#onUpdate(UUID, Map)}.
     *
     * @param player The player to select the cosmetic for.
     * @param cosmetic The {@link Cosmetic} to select.
     * @param forceSelect Whether to force select the cosmetic. Ticking will still be done,
     *                    regardless of this option.
     *
     * @see CosmeticManager#select(Map, String, boolean)
     */
    public void select(Player player, Cosmetic cosmetic, boolean forceSelect) {
        select(player, cosmetic, forceSelect, false);
    }

    /**
     * Unselects a specific cosmetic in the specified cosmetics.
     *
     * @param cosmetics The cosmetics used to unselect the specific cosmetic.
     * @param id The ID of the cosmetic to unselect.
     * @return The updated map with the unselected cosmetic.
     */
    public Map<Cosmetic, Boolean> unselect(Map<Cosmetic, Boolean> cosmetics, String id) {
        CompletableFuture<Cosmetic> toSelect = new CompletableFuture<>();
        cosmetics.forEach((cosmetic, selected) -> {
            if (cosmetic.getId().equals(id))
                toSelect.complete(cosmetic);
        });

        if (toSelect.getNow(null) != null)
            cosmetics.replace(toSelect.getNow(null), false);

        return cosmetics;
    }

    /**
     * Unselects a cosmetic for a player. Unlike {@link CosmeticManager#unselect(Map, String)},
     * this method manages the removal of ticking the cosmetic for the player.
     *
     * @param player The player to unselect the cosmetic for.
     * @param cosmetic The cosmetic to unselect.
     * @param update Whether to call {@link CosmeticManager#onUpdate(UUID, Map)}.
     */
    public void unselect(Player player, Cosmetic cosmetic, boolean update) {
        Map<Cosmetic, Boolean> cosmetics = getCosmetics(player.getUniqueId());
        if(cosmetics != null) {
            unselect(cosmetics, cosmetic.getId());

            if(cosmetic instanceof RunnableCosmetic) {
                removePlayerFromCosmeticTicking(player, cosmetic);
            }

            if(update)
                onUpdate(player.getUniqueId(), convert(cosmetics));
        }
    }

    /**
     * Unselects a cosmetic for a player. Unlike {@link CosmeticManager#unselect(Map, String)},
     * this method manages the removal of ticking the cosmetic for the player.
     *
     * This method will not call {@link CosmeticManager#onUpdate(UUID, Map)}.
     *
     * @param player The player to unselect the cosmetic for.
     * @param cosmetic The cosmetic to unselect.
     *
     * @see CosmeticManager#unselect(Player, Cosmetic, boolean)
     */
    public void unselect(Player player, Cosmetic cosmetic) {
        unselect(player, cosmetic, false);
    }

    /**
     * Checks if a {@link Cosmetic} is in the specified cosmetics. Unlike
     * {@link Map#containsKey(Object)}, this method checks the ID instead of
     * the hashcode.
     *
     * @param cosmetics The cosmetics to check.
     * @param cosmetic The cosmetic to check for.
     * @return TRUE if the cosmetic exists, FALSE otherwise.
     */
    public boolean hasCosmetic(Map<Cosmetic, Boolean> cosmetics, Cosmetic cosmetic) {
        AtomicBoolean contains = new AtomicBoolean(false);
        cosmetics.forEach((c, selected) -> {
            if(c.getId().equals(cosmetic.getId()))
                contains.set(true);
        });

        return contains.get();
    }

    /**
     * Returns whether a cosmetic is selected in the specified cosmetics.
     *
     * @param cosmetics The cosmetics to check.
     * @param cosmetic The {@link Cosmetic} to check for.
     * @return TRUE if the cosmetic is selected, FALSE otherwise.
     */
    public boolean isCosmeticSelected(Map<Cosmetic, Boolean> cosmetics, Cosmetic cosmetic) {
        AtomicBoolean selected = new AtomicBoolean(false);
        cosmetics.forEach((c, s) -> {
            if(c.getId().equals(cosmetic.getId()))
                selected.set(s);
        });

        return selected.get();
    }

    /**
     * Checks whether an {@link AeroPlayer} is allowed to purchase the specified
     * {@link Cosmetic}.
     *
     * @param player The player to check.
     * @param cosmetic The cosmetic to check.
     * @return TRUE if the player is allowed to purchase the cosmetic, FALSE
     * otherwise.
     *
     * @apiNote This method is in {@link Beta}, and is extremely volatile. Use
     * with caution.
     */
    @Beta
    public boolean isPurchaseAllowed(AeroPlayer player, Cosmetic cosmetic) {
        if(cosmetic instanceof NonPurchasableCosmetic)
            return false;

        if(cosmetic instanceof RankCosmetic) {
            RankCosmetic rc = (RankCosmetic) cosmetic;

            if((player.getRank().ordinal() > rc.getMinRank().ordinal())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether an {@link AeroPlayer} is allowed to select the specified
     * {@link Cosmetic}.
     *
     * @param player The player to check.
     * @param cosmetic The cosmetic to check.
     * @return TRUE if the player is allowed to select the cosmetic, FALSE
     * otherwise.
     *
     * @apiNote This method is in {@link Beta}, and is extremely volatile. Use
     * with caution.
     */
    @Beta
    public boolean isSelectAllowed(AeroPlayer player, Cosmetic cosmetic) {
        Map<Cosmetic, Boolean> cosmetics = getCosmetics(player.getUuid());

        if(cosmetics == null)
            return false;

        if(cosmetic instanceof RankCosmetic) {
            RankCosmetic rc = (RankCosmetic) cosmetic;

            if(player.getRank().ordinal() > rc.getMinRank().ordinal()) {
                return false;
            } else if(rc.isDefaultRankCosmetic()) {
                return true;
            }
        }

        if(!hasCosmetic(cosmetics, cosmetic))
            return false;

        return true;
    }

    /**
     * Gets the {@link AeroInventory} associated with this manager. Once
     * {@link AeroInventory#openInventory(Player)} is invoked on the returned
     * menu, all menu related processes will be handled internally.
     *
     * @return The menu associated with this manager.
     */
    public AeroInventory getCosmeticMenu() {
        return new CosmeticCategoryMenu(this);
    }

    /**
     * Called when an internal process requires the latest fetched cosmetics
     * for the specified {@link UUID}.
     *
     * @param uuid The {@link UUID} of the player to get the cosmetics for.
     * @return The {@link Map} keyed by {@link Cosmetic} with boolean values
     * specifying whether the cosmetic is selected.
     */
    public abstract Map<Cosmetic, Boolean> getCosmetics(UUID uuid);

    /**
     * Called when a player's cosmetic info has been updated, and requires
     * updating the database.
     *
     * @param uuid The {@link UUID} of the player.
     * @param cosmetics The {@link Map} of cosmetics that were updated.
     */
    public abstract void onUpdate(UUID uuid, Map<String, Boolean> cosmetics);

    /**
     * Called when a player requests to purchase the specified cosmetic.
     *
     * @param uuid the {@link UUID} of the player.
     * @param cosmetic The {@link Cosmetic} the player requested to purchase.
     * @return Whether the player is allowed to purchase the specified
     * cosmetic. If TRUE is returned, the internal process calling this
     * method will update the player's cosmetics and invoke
     * {@link CosmeticManager#onUpdate(UUID, Map)}. If FALSE is returned,
     * the internal process will cancel the request.
     */
    public abstract boolean onPurchase(UUID uuid, Cosmetic cosmetic);

    /**
     * Called when a player requests to select the specified cosmetic.
     *
     * @param uuid The {@link UUID} of the player.
     * @param cosmetic The {@link Cosmetic} the player requested to select.
     * @return Whether the player is allowed to select the specified {@link Cosmetic}.
     * If TRUE is returned, the internal process calling this method will
     * update the player's cosmetics and invoke {@link CosmeticManager#onUpdate(UUID, Map)}.
     * If FALSE is returned, the internal process wil cancel the request.
     */
    public abstract boolean onSelect(UUID uuid, Cosmetic cosmetic);

    /**
     * Called when a player requests to unselect the specified cosmetic.
     *
     * @param uuid The {@link UUID} of the player.
     * @param cosmetic The {@link Cosmetic} the player requested to unselect.
     */
    public abstract void onUnselect(UUID uuid, Cosmetic cosmetic);

    // LISTENERS

    // Enables ticking for cosmetics the player has selected
    @EventHandler
    public final void onPlayerJoin(PlayerJoinEvent e) {
        Map<Cosmetic, Boolean> cosmetics = getCosmetics(e.getPlayer().getUniqueId());
        if(cosmetics != null)
            cosmetics.forEach((c, selected) -> {
                if(selected && c instanceof RunnableCosmetic) {
                    RunnableCosmetic rc = (RunnableCosmetic) c;
                    rc.onSelect(e.getPlayer());
                    addPlayerToCosmeticTicking(e.getPlayer(), c);
                }
            });
    }
}
