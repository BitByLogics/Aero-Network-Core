package net.aeronetwork.core.inventory.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.aeronetwork.core.inventory.call.InventoryCall;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class InventoryItemInfo {

    private ItemStack item;
    private int slot;
    private boolean ignoreEvent;
    private InventoryCall call;
}
