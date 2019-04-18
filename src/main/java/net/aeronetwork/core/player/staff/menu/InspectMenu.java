package net.aeronetwork.core.player.staff.menu;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class InspectMenu extends AeroInventory {

    public InspectMenu(AeroPlayer player) {
        super("Inspecting " + player.getAccountName(), InventorySize.ONE_ROW, null);
        addItem(new ItemStackBuilder(Material.SKULL_ITEM, ItemStackBuilder.ItemType.SKULL)
                .setName("§c" + player.getAccountName())
                .addLore(" ",
                        "§eAccount Name: §c" + player.getAccountName(),
                        "§eRank: §c" + player.getRank().getFriendlyName(),
                        "§eMuted: §c" + AeroCore.PUNISHMENT_MANAGER.isMuted(player.getUuid()),
                        "§eDisguised: §c" + player.isDisguised(),
                        "§eVanished: §c" + player.isVanished(),
                        "§ePlay Time: §c" + TimeUnit.MILLISECONDS.convert(player.getPlayTime(), TimeUnit.HOURS),
                        "§eDiscord Linked: §c" + (player.getDiscordId() == null ? "Not Linked" : "Linked"),
                        "§eAlts: §cNone",
                        "§ePunishments: §c" + player.getPunishments().size(),
                        " ")
                .setOwner(player.getAccountName())
                .get()
        , 4);
    }

    @Override
    public void onClick(ClickData data) {

    }

    @Override
    public void onClose(Player player) {

    }
}
