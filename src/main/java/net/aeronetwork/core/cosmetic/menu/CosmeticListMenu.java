package net.aeronetwork.core.cosmetic.menu;

import com.google.common.collect.Lists;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.cosmetic.*;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CosmeticListMenu extends AeroInventory {

    private CosmeticManager manager;
    private Enum<? extends CosmeticType> type;
    private UUID uuid;

    private CosmeticType cosmeticType;

    private final int[] PANE_SLOTS = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35,
                                     36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private final int ICON_SLOT = 4;
    private final int GO_BACK_SLOT = 49;

    public CosmeticListMenu(CosmeticManager manager, Enum<? extends CosmeticType> type, UUID uuid) {
        super("Cosmetics", InventorySize.SIX_ROWS, null);

        this.manager = manager;
        this.type = type;
        this.uuid = uuid;

        this.cosmeticType = (CosmeticType) type;

        setName(this.cosmeticType.getFriendlyName() + " Category");
    }

    @Override
    public void load() {
        AeroPlayer player = AeroCore.PLAYER_MANAGER.getPlayer(this.uuid);

        // Beautification
        Arrays.stream(PANE_SLOTS).forEach(slot ->
                addItem(new ItemStackBuilder(
                                Material.STAINED_GLASS_PANE,
                                1,
                                (short) 7,
                                ItemStackBuilder.ItemType.NORMAL)
                                .setName(" ")
                                .get(),
                        slot
                )
        );

        ItemStackBuilder builder;
        if (this.cosmeticType.getIcon() != null) {
            builder = new ItemStackBuilder(this.cosmeticType.getIcon());
        } else {
            builder = new ItemStackBuilder(Material.COAL, ItemStackBuilder.ItemType.NORMAL);
        }

        builder.setName("§6Viewing " + this.cosmeticType.getFriendlyName());
        builder.setLore(Lists.newArrayList(
                " ",
                "§eCrystals: §3" + player.getCrystals(),
                " ",
                "§7Crystals are used to buy cosmetics.",
                "§7Earn more by playing games!",
                " ",
                "§7Visit §estore.aeronetwork.net §7to",
                "§7purchase ranks, loot chests, and more!"
        ));

        addItem(builder.get(), ICON_SLOT);

        addItem(new ItemStackBuilder(Material.BARRIER, ItemStackBuilder.ItemType.NORMAL)
                .setName("§eGo Back")
                .addLore(" ", "§7Go back to the categories.")
                .get(),
                GO_BACK_SLOT,
                data -> new CosmeticCategoryMenu(this.manager).openInventory(data.getPlayer())
        );


        // Cosmetic related stuff
        Map<Cosmetic, Boolean> cosmetics = manager.getCosmetics(uuid);
        if(cosmetics == null)
            return;


        List<Cosmetic> categoryCosmetics = Lists.newArrayList();

        manager.getCosmetics().forEach(cosmetic -> {
            if(cosmetic.getType() == this.type)
                categoryCosmetics.add(cosmetic);
        });

        categoryCosmetics.forEach(cosmetic -> {
            ItemStack icon = cosmetic.getIcon() != null ? cosmetic.getIcon() : new ItemStack(Material.COAL);

            // Cosmetic related variables
            boolean purchasable = !(cosmetic instanceof NonPurchasableCosmetic);
            boolean multiSelectable = cosmetic instanceof MultiSelectableCosmetic;
            RankCosmetic rc = (cosmetic instanceof RankCosmetic ? (RankCosmetic) cosmetic : null);

            boolean purchased = manager.hasCosmetic(cosmetics, cosmetic);
            boolean selected = manager.isCosmeticSelected(cosmetics, cosmetic);

            List<String> lore = Lists.newArrayList(" ");
            lore.add("§7" + cosmetic.getDescription());
            lore.add(" ");

            if(purchasable && !purchased) {
                lore.add("§7Cost: §3" + cosmetic.getCost() + " Crystal" + (cosmetic.getCost() > 1 ? "s" : ""));
                lore.add("§3§oYou have " + player.getCrystals() + " Crystals");
            }

            if(rc != null) {
                lore.add("§7This cosmetic requires the");
                lore.add(rc.getMinRank().getColor() + rc.getMinRank().getFriendlyName() + "§7 rank!");

                if(rc.isDefaultRankCosmetic()) {
                    lore.add(" ");
                    lore.add("§7This cosmetic is FREE for");
                    lore.add("§7everyone with " + rc.getMinRank().getFriendlyName() + " or");
                    lore.add("§7higher!");
                }
            }

            if(multiSelectable) {
                lore.add(" ");
                lore.add("§7§oThis cosmetic can be selected");
                lore.add("§7§owith other cosmetics of this type!");
            }

            if(purchased || manager.isSelectAllowed(player, cosmetic)) {
                if(!selected) {
                    lore.add("§aClick to select!");
                } else {
                    lore.add("§cClick to unselect!");
                }
            } else if(manager.isPurchaseAllowed(player, cosmetic)) {
                lore.add(" ");
                lore.add("§eClick to purchase!");
            }

            addItem(new ItemStackBuilder(icon)
                            .setName("§6" + cosmetic.getName() + (selected ? " §a§lSELECTED" : ""))
                            .setLore(lore)
                            .get(),
                    data -> {
                        Player p = Bukkit.getServer().getPlayer(uuid);
                        if(manager.isSelectAllowed(player, cosmetic)) {
                            // Event
                            boolean select = manager.onSelect(uuid, cosmetic);

                            if(select) {
                                if(!selected) {
                                    manager.select(p, cosmetic, true, true);

                                    if(p != null)
                                        p.sendMessage(FM.mainFormat("Cosmetics",
                                                "§eYou have selected §a" + cosmetic.getName() + "§e!"));
                                } else {
                                    manager.onUnselect(uuid, cosmetic);
                                    manager.unselect(p, cosmetic, true);

                                    if(p != null)
                                        p.sendMessage(FM.mainFormat("Cosmetics",
                                                "§eYou have unselected §a" + cosmetic.getName() + "§e!"));
                                }
                            }
                        } else if(manager.isPurchaseAllowed(player, cosmetic)) {
                            Map<Cosmetic, Boolean> newFetched = manager.getCosmetics(uuid);
                            if(!manager.hasCosmetic(newFetched, cosmetic)) {
                                boolean purchase = manager.onPurchase(uuid, cosmetic);

                                if(purchase) {
                                    newFetched.put(cosmetic, false);
                                    manager.onUpdate(uuid, manager.convert(newFetched));

                                    if(p != null)
                                        p.sendMessage(FM.mainFormat("Cosmetics",
                                            "§eYou have purchased §a" + cosmetic.getName() + "§e!"));
                                } else {
                                    if(p != null)
                                        p.sendMessage(FM.mainFormat("Cosmetics",
                                                "§cYou can't afford this cosmetic."));
                                }
                            }
                        } else {
                            if(p != null)
                                p.sendMessage(FM.mainFormat("Cosmetics",
                                        "§cYou are not allowed to use this cosmetic!"));
                        }

                        this.getItems().clear();
                        updateInventory();
                    }

            );
        });
    }

    @Override
    public void onClick(ClickData data) {

    }

    @Override
    public void onClose(Player player) {

    }
}
