package net.aeronetwork.core.player.punishment.menu;

import lombok.Getter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.call.InventoryCall;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.Punishment;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainPunishMenu extends AeroInventory {

    @Getter
    private String player;
    @Getter
    private String reason;
    @Getter
    private Punishment.PunishmentType punishmentType;

    public MainPunishMenu(String player, String reason) {
        super("§c§lPunish §e" + player, InventorySize.SIX_ROWS, null);
        this.player = player;
        this.reason = reason;
        loadInventoryButtons();
    }

    public void loadInventoryButtons() {
        // Player's skull
        addItem(new ItemStackBuilder(Material.SKULL_ITEM, ItemStackBuilder.ItemType.SKULL).setData((short) 3)
                .setOwner(player).setName("§4§l" + player).setLore(new String[] {"", "§cReason§8: §e" + reason,
                        "§cPrevious Punishments§8: §e" + AeroCore.PLAYER_MANAGER.getPlayer(player)
                                .getPunishments().size()}).build().get(), 4);

        // History Button
        addItem(new ItemStackBuilder(Material.PAPER, ItemStackBuilder.ItemType.NORMAL).setName("§a§lPunishment History")
                        .setLore(new String[]{"§eView the player's punishment history."}).build().get(),
                10, new InventoryCall() {
                    @Override
                    public void onCall(ClickData data) {
                        AeroPlayer aeroPlayer = AeroCore.PLAYER_MANAGER.getPlayer(player);
                        if(aeroPlayer != null)
                            new PunishmentHistoryMenu(aeroPlayer).openInventory(data.getPlayer());
                    }
                });

        // Custom duration and warning buttons
        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lBan §8- §c§lCustom Length").setLore(new String[] {"§eBan the player with a custom time duration."})
                .setData((short) 14).build().get(), 12, data -> {
            data.getPlayer().closeInventory();

            punishmentType = Punishment.PunishmentType.PERMANENT_BAN;

            AeroPlayer aeroPlayer = AeroCore.PLAYER_MANAGER.getPlayer(data.getPlayer().getUniqueId());

            aeroPlayer.getSessionObjects().put(data.getPlayer().getUniqueId().toString(), this);
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lMute §8- §c§lCustom Length").setLore(new String[] {"§eMute the player with a custom time duration."})
                .setData((short) 1).build().get(), 21, data -> {
            data.getPlayer().closeInventory();

            punishmentType = Punishment.PunishmentType.PERMANENT_MUTE;

            AeroPlayer aeroPlayer = AeroCore.PLAYER_MANAGER.getPlayer(data.getPlayer().getUniqueId());

            aeroPlayer.getSessionObjects().put(data.getPlayer().getUniqueId().toString(), this);
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lWarn").setLore(new String[]{"§eWarn the player."})
                .setData((short) 5).build().get(), 30, data -> {
                    data.getPlayer().closeInventory();
                    AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.WARNING, reason, System.currentTimeMillis(), -1, true));
                });

        // Ban Times
        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lBan §8- §c§lPermanent").setLore(new String[]{"§ePermanently ban the player."})
                .setData((short) 14).build().get(), 14, data -> {
                    data.getPlayer().closeInventory();
                    AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.PERMANENT_BAN, reason, System.currentTimeMillis(), -1, true));
                });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lBan §8- §c§l3 Months").setLore(new String[] {"§eBan the player for 3 months."})
                .setData((short) 1).build().get(), 23, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_BAN, reason, System.currentTimeMillis(), 2592000000L, true));
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lBan §8- §c§l1 Week").setLore(new String[] {"§eBan the player for 1 week."})
                .setData((short) 4).build().get(), 32, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_BAN, reason, System.currentTimeMillis(), 604800000, true));
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lBan §8- §c§l1 Day").setLore(new String[] {"§eBan the player for 1 day."})
                .setData((short) 5).build().get(), 41, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_BAN, reason, System.currentTimeMillis(), 86400000, true));
        });

        // Mute Times
        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lMute §8- §c§lPermanent").setLore(new String[]{"§ePermanently mute the player."})
                .setData((short) 14).build().get(), 16, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.PERMANENT_MUTE, reason, System.currentTimeMillis(), -1, true));
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lMute §8- §c§l1 Week").setLore(new String[] {"§eMute the player for 1 week."})
                .setData((short) 1).build().get(), 25, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_MUTE, reason, System.currentTimeMillis(), 604800000, true));
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lMute §8- §c§l1 Day").setLore(new String[] {"§eMute the player for 1 day."})
                .setData((short) 4).build().get(), 34, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_MUTE, reason, System.currentTimeMillis(), 86400000, true));
        });

        addItem(new ItemStackBuilder(Material.STAINED_CLAY, ItemStackBuilder.ItemType.NORMAL)
                .setName("§4§lMute §8- §c§l5 hours").setLore(new String[] {"§eMute the player for 5 hours."})
                .setData((short) 5).build().get(), 43, data -> {
            data.getPlayer().closeInventory();
            AeroCore.PUNISHMENT_MANAGER.punish(AeroCore.PLAYER_MANAGER.getPlayer(player), new Punishment(data.getPlayer().getUniqueId(), Punishment.PunishmentType.TEMPORARY_MUTE, reason, System.currentTimeMillis(), 18000000, true));
        });
    }

    @Override
    public void onClick(ClickData data) {
    }

    @Override
    public void onClose(Player player) {

    }
}
