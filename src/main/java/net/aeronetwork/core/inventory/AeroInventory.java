package net.aeronetwork.core.inventory;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.inventory.call.InventoryCall;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.info.InventoryItemInfo;
import net.aeronetwork.core.inventory.size.InventorySize;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Defines the base for all Aero inventories.
 */
@Getter
@Setter
public abstract class AeroInventory implements Listener {

    private String name;
    private InventorySize size;
    @Setter(AccessLevel.NONE)
    private List<InventoryItemInfo> items;

    private boolean cancelInventoryClicking;

    @Setter(AccessLevel.NONE)
    private Inventory builtInventory;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final int ADD_SLOT_VALUE = -1;

    /**
     * Creates a new instance of AeroInventory and sets up
     * all required resources for the inventory to be functional.
     *
     * @param name The name of the inventory.
     * @param size The size of the inventory.
     * @param items All items the inventory should have. This parameter
     *              can be left null, and one of the various available
     *              methods can be used to add items.
     */
    public AeroInventory(String name, InventorySize size, @Nullable List<InventoryItemInfo> items) {
        this.name = name;
        this.size = size;
        this.items = (items == null ? Lists.newArrayList() : items);

        this.cancelInventoryClicking = true;

        Bukkit.getServer().getPluginManager().registerEvents(this, AeroCore.INSTANCE);
    }

    /**
     * Adds a new ItemStack to the inventory.
     *
     * @param item The item to add.
     */
    public void addItem(ItemStack item) {
        items.add(new InventoryItemInfo(item, ADD_SLOT_VALUE, false, null));
    }

    /**
     * Adds a new ItemStack to the inventory.
     *
     * @param item The item to add.
     * @param slot The slot to set the item to.
     */
    public void addItem(ItemStack item, int slot) {
        items.add(new InventoryItemInfo(item, slot, false, null));
    }

    /**
     * Adds a new ItemStack to the inventory.
     *
     * @param item The item to add.
     * @param call The callback for when the item is clicked.
     */
    public void addItem(ItemStack item, InventoryCall call) {
        items.add(new InventoryItemInfo(item, ADD_SLOT_VALUE, false, call));
    }

    /**
     * Adds a new ItemStack to the inventory.
     *
     * @param item The item to add.
     * @param slot The slot to set the item to.
     * @param call The callback for when the item is clicked.
     */
    public void addItem(ItemStack item, int slot, InventoryCall call) {
        items.add(new InventoryItemInfo(item, slot, false, call));
    }

    /**
     * Adds a new ItemStack to the inventory.
     *
     * @param item The item to add.
     * @param slot The slot to set the item to.
     * @param ignoreEvent Whether to ignore {@link AeroInventory#onClick(ClickData)}.
     * @param call The callback for when the item is clicked.
     */
    public void addItem(ItemStack item, int slot, boolean ignoreEvent, InventoryCall call) {
        items.add(new InventoryItemInfo(item, slot, ignoreEvent, call));
    }

    /**
     * Builds the inventory with the inputted items.
     *
     * @return An instance of the inventory.
     */
    public Inventory buildInventory() {
        this.builtInventory = Bukkit.getServer().createInventory(null, size.getSize(), name);

        load();
        items.forEach(item -> {
            if(item.getSlot() != ADD_SLOT_VALUE) {
                builtInventory.setItem(item.getSlot(), item.getItem());
            } else {
                builtInventory.addItem(item.getItem());
            }
        });

        return builtInventory;
    }

    /**
     * Updates the built inventory, and reopens the new inventory
     * for all viewers.
     */
    public void updateInventory() {
        List<HumanEntity> viewers = this.builtInventory != null ?
                Lists.newCopyOnWriteArrayList(builtInventory.getViewers()) : Lists.newCopyOnWriteArrayList();
        buildInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                for(HumanEntity viewer : viewers) {
                    Bukkit.getServer().getPlayer(viewer.getUniqueId()).openInventory(builtInventory);
                }
            }
        }.runTaskLater(AeroCore.INSTANCE, 1);
    }

    /**
     * Updates all items in the inventory.
     */
    public void updateItems() {
        this.items.clear();
        load();
        updateInventory();
    }

    /**
     * Opens the built inventory for the specified player.
     * If there is no built inventory, {@link AeroInventory#buildInventory()}
     * will be called.
     *
     * @param player The player to open the inventory for.
     */
    public void openInventory(Player player) {
        if(builtInventory == null)
            buildInventory();

        //TODO: Check if this listener is registered, if not, register it (can be done by looping through
        //TODO: registered listeners).
        player.openInventory(builtInventory);
    }

    /**
     * Called when {@link AeroInventory#updateItems()} is invoked. This
     * method should setup the inventory as required.
     *
     * Before this method is called, the inventory will be empty.
     */
    public void load() {
    }

    /**
     * Called when an item is clicked in the inventory.
     *
     * @param data All data associated with the click.
     */
    public abstract void onClick(ClickData data);

    /**
     * Called when a player closes the inventory.
     *
     * @param player The player that closed the inventory.
     */
    public abstract void onClose(Player player);

    /* LISTENERS */

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if(builtInventory != null && builtInventory.getViewers().contains(p) && item != null) {
            if(cancelInventoryClicking)
                e.setCancelled(true);
            InventoryItemInfo info = items.stream()
                    .filter(i -> i.getItem() != null && i.getItem().hashCode() == item.hashCode())
                    .findFirst()
                    .orElse(null);
            ClickData data = new ClickData(
                    p,
                    builtInventory,
                    item,
                    e.getAction(),
                    e.getClick(),
                    e.getSlot(),
                    e.getHotbarButton()
            );
            if(info != null && info.getCall() != null) {
                info.getCall().onCall(data);
            }
            onClick(data);
        }
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(builtInventory != null && builtInventory.getViewers().contains(p) &&
                e.getInventory().getViewers().size() - 1 == 0) {
            HandlerList.unregisterAll(this);
            onClose(p);
        }
    }
}
