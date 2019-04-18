package net.aeronetwork.core.inventory.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ClickData {

    private Player player;
    private Inventory inventory;
    private ItemStack item;
    private InventoryAction action;
    private ClickType clickType;
    private int slot;
    private int hotbarButton;
}
