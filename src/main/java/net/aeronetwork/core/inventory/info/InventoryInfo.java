package net.aeronetwork.core.inventory.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.aeronetwork.core.inventory.AeroInventory;

import java.util.UUID;

@AllArgsConstructor
@Data
public class InventoryInfo {

    private UUID uuid;
    private AeroInventory inventory;
}
