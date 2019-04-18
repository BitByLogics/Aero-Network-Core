package net.aeronetwork.core.player.staff;

import com.google.common.collect.Maps;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.manager.Manager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.player.staff.command.InspectCommand;
import net.aeronetwork.core.player.staff.command.StaffCommand;
import net.aeronetwork.core.player.staff.menu.InspectMenu;
import net.aeronetwork.core.player.staff.menu.PunishMenu;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class StaffManager extends Manager {

    private HashMap<UUID, Inventory> inventoryCache = Maps.newHashMap();

    private ItemStack vanishOffItem;
    private ItemStack vanishOnItem;
    private ItemStack inspectItem;
    private ItemStack punishItem;

    public StaffManager(JavaPlugin plugin) {
        super("Staff", "Manage all actions and listeners related to the staff system.", plugin);
        vanishOffItem = new ItemStackBuilder(Material.INK_SACK, ItemStackBuilder.ItemType.NORMAL)
                .setName("§cVanish §8- §8(§4Visible§8)")
                .addLore(" ",
                        "§eClick to toggle vanish.",
                        " ")
                .setData((short) 8)
                .get();

        vanishOnItem = new ItemStackBuilder(Material.INK_SACK, ItemStackBuilder.ItemType.NORMAL)
                .setName("§cVanish §8- §8(§aHidden§8)")
                .addLore(" ",
                        "§eClick to toggle vanish.",
                        " ")
                .setData((short) 10)
                .get();

        inspectItem = new ItemStackBuilder(Material.PAPER, ItemStackBuilder.ItemType.NORMAL)
                .setName("§cInspect Player")
                .addLore(" ",
                        "§eInspect the player, view their information.",
                        " ")
                .get();

        punishItem = new ItemStackBuilder(Material.IRON_BARDING, ItemStackBuilder.ItemType.NORMAL)
                .setName("§cPunish Player")
                .addLore(" ",
                        "§ePunish the player.",
                        " ")
                .get();

        AeroCore.COMMAND_MANAGER.registerCommand(StaffCommand.class);
        AeroCore.COMMAND_MANAGER.registerCommand(InspectCommand.class);
    }

    public void toggleStaffMode(AeroPlayer player) {
        if(player.isInStaffMode()) {
            exitStaffMode(player);
        } else {
            enterStaffMode(player);
        }
    }


    public void enterStaffMode(AeroPlayer player) {
        player.updateStaffMode(true);
        Player bukkitPlayer = player.getBukkitPlayer();
        inventoryCache.put(player.getUuid(), bukkitPlayer.getInventory());

        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setItem(2, player.isVanished() ? vanishOnItem : vanishOffItem);
        bukkitPlayer.getInventory().setItem(4, inspectItem);
        bukkitPlayer.getInventory().setItem(6, punishItem);
        player.sendMessage(FM.mainFormat("Staff Mode", "You're now in staff mode."));
    }

    public void exitStaffMode(AeroPlayer player) {
        player.updateStaffMode(false);
        Player bukkitPlayer = player.getBukkitPlayer();
        bukkitPlayer.getInventory().clear();
        if(inventoryCache.containsKey(player.getUuid())) {
            Inventory cachedInventory = inventoryCache.get(player.getUuid());

            for(int i = 0; i < cachedInventory.getSize(); i++) {
                if(cachedInventory.getItem(i) != null && cachedInventory.getItem(i).getType() != Material.AIR) {
                    bukkitPlayer.getInventory().setItem(i, cachedInventory.getItem(i));
                }
            }

            inventoryCache.remove(player.getUuid());
        }
        if(player.isVanished()) {
            player.updateVanished(false);
            updatePlayers(player);
            player.sendMessage(FM.mainFormat("Vanish", "You're no longer vanished."));
        }
        player.sendMessage(FM.mainFormat("Staff Mode", "You're no longer in staff mode."));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getType() == InventoryType.PLAYER && AeroCore.PLAYER_MANAGER.getPlayer(event.getWhoClicked().getUniqueId()).isInStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId()).isInStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId()).isInStaffMode()) {
            if(event.getItem() != null) {
                AeroPlayer player = AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId());
                if(event.getItem().getItemMeta().hashCode() == vanishOffItem.getItemMeta().hashCode()) {
                    player.updateVanished(true);
                    player.sendMessage(FM.mainFormat("Vanish", "You're now vanished."));
                    updatePlayers(player);
                    event.getPlayer().setItemInHand(vanishOnItem);
                    event.setCancelled(true);
                }

                if(event.getItem().getItemMeta().hashCode() == vanishOnItem.getItemMeta().hashCode()) {
                    player.updateVanished(false);
                    player.sendMessage(FM.mainFormat("Vanish", "You're no longer vanished."));
                    updatePlayers(player);
                    event.getPlayer().setItemInHand(vanishOffItem);
                    event.setCancelled(true);
                }

                if(event.getItem().getItemMeta().hashCode() == inspectItem.getItemMeta().hashCode() ||
                        event.getItem().getItemMeta().hashCode() == punishItem.getItemMeta().hashCode()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClickPlayer(PlayerInteractAtEntityEvent event) {
        if(AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId()).isInStaffMode() && event.getRightClicked() instanceof Player) {
            if(event.getPlayer().getItemInHand() != null) {
                AeroPlayer player = AeroCore.PLAYER_MANAGER.getPlayer(event.getPlayer().getUniqueId());

                if(event.getPlayer().getItemInHand().getItemMeta().hashCode() == inspectItem.getItemMeta().hashCode()) {
                    event.setCancelled(true);
                    new InspectMenu(AeroCore.PLAYER_MANAGER.getPlayer(event.getRightClicked().getUniqueId())).openInventory(event.getPlayer());
                }

                if(event.getPlayer().getItemInHand().getItemMeta().hashCode() == punishItem.getItemMeta().hashCode()) {
                    event.setCancelled(true);
                    new PunishMenu(player, AeroCore.PLAYER_MANAGER.getPlayer(event.getRightClicked().getUniqueId())).openInventory(event.getPlayer());
                }
            }
        }
    }

    public void updatePlayers(AeroPlayer player) {
        if(player.isVanished()) {
            for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                if(bukkitPlayer != player.getBukkitPlayer() && bukkitPlayer.canSee(player.getBukkitPlayer())) {
                    AeroPlayer target = AeroCore.PLAYER_MANAGER.getPlayer(bukkitPlayer.getUniqueId());
                    if(target.getRank().getRankType().equals(player.getRank().getRankType())
                            || target.getRank().getRankType().equals(Rank.RankType.HIGH_STAFF)
                            || target.getRank().getPriority() < player.getRank().getPriority()) {

                    } else {
                        bukkitPlayer.hidePlayer(player.getBukkitPlayer());
                    }
                }
            }
        } else {
            for(Player bukkitPlayer : Bukkit.getOnlinePlayers()) {
                if(bukkitPlayer != player.getBukkitPlayer()) {
                    bukkitPlayer.showPlayer(player.getBukkitPlayer());
                }
            }
        }
    }

}
