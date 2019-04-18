package net.aeronetwork.core.player.staff.menu;

import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.menu.MainPunishMenu;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishMenu extends AeroInventory {

    public PunishMenu(AeroPlayer punisher, AeroPlayer player) {
        super("Punish " + player.getAccountName(), InventorySize.ONE_ROW, null);
        addItem(
                new ItemStackBuilder(Material.DIAMOND_ORE, ItemStackBuilder.ItemType.NORMAL)
                        .setName("§cHacking §8(§eX-Ray§8)")
                        .addLore(" ",
                                "§ePunish the player for using x-ray.",
                                " ")
                        .get()
                , 2, data -> new MainPunishMenu(player.getAccountName(), "Hacking: X-Ray").openInventory(punisher.getBukkitPlayer()));

        addItem(
                new ItemStackBuilder(Material.DIAMOND_ORE, ItemStackBuilder.ItemType.NORMAL)
                        .setName("§cHacking §8(§eMalicious Hacks§8)")
                        .addLore(" ",
                                "§ePunish the player for using combat hacks.",
                                " ")
                        .get()
                , 4, data -> new MainPunishMenu(player.getAccountName(), "Hacking: Malicious Hacks").openInventory(punisher.getBukkitPlayer()));

        addItem(
                new ItemStackBuilder(Material.DIAMOND_ORE, ItemStackBuilder.ItemType.NORMAL)
                        .setName("§cHacking §8(§eExploiting§8)")
                        .addLore(" ",
                                "§ePunish the player for exploiting.",
                                " ")
                        .get()
                , 6, data -> new MainPunishMenu(player.getAccountName(), "Abusing Exploits").openInventory(punisher.getBukkitPlayer()));
    }

    @Override
    public void onClick(ClickData data) {

    }

    @Override
    public void onClose(Player player) {

    }
}
