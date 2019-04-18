package net.aeronetwork.core.cosmetic.menu;

import com.google.common.collect.Lists;
import net.aeronetwork.core.cosmetic.CosmeticManager;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class CosmeticCategoryMenu extends AeroInventory {

    private CosmeticManager manager;

    private final int[] slots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29,
                                 30, 31, 32, 33, 37, 38, 39, 40, 41, 42};

    public CosmeticCategoryMenu(CosmeticManager manager) {
        super("Categories", InventorySize.SIX_ROWS, null);
        this.manager = manager;
    }

    @Override
    public void load() {
        AtomicInteger currentSlot = new AtomicInteger(0);
        manager.getAllTypes().forEach(type -> {
            String friendlyName;
            ItemStack icon;

            try {
                Method name = type.getClass().getMethod("getFriendlyName");
                friendlyName = (String) name.invoke(type);

                Method i = type.getClass().getMethod("getIcon");
                icon = (ItemStack) i.invoke(type);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if(currentSlot.get() < slots.length) {
                if (icon != null) {
                    addItem(new ItemStackBuilder(icon)
                            .setName((friendlyName != null ? "§a" + friendlyName : "§cUnknown"))
                            .setLore(Lists.newArrayList(
                                    " ",
                                    "§7Click to view this",
                                    "§7category!"
                            ))
                            .get(),
                            slots[currentSlot.get()],
                            data -> new CosmeticListMenu(manager, type, data.getPlayer().getUniqueId())
                                    .openInventory(data.getPlayer())
                    );
                } else {
                    addItem(new ItemStackBuilder(Material.COAL, ItemStackBuilder.ItemType.NORMAL)
                            .setName((friendlyName != null ? "§a" + friendlyName : "§cUnknown"))
                            .setLore(Lists.newArrayList(
                                    " ",
                                    "§7Click to view this",
                                    "§7category!"
                            ))
                            .get(),
                            slots[currentSlot.get()],
                            data -> new CosmeticListMenu(manager, type, data.getPlayer().getUniqueId())
                                    .openInventory(data.getPlayer())
                    );
                }
                currentSlot.incrementAndGet();
            }
        });
    }

    @Override
    public void onClick(ClickData data) {

    }

    @Override
    public void onClose(Player player) {

    }
}
