package net.aeronetwork.core.player.punishment.menu;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.inventory.AeroInventory;
import net.aeronetwork.core.inventory.info.ClickData;
import net.aeronetwork.core.inventory.size.InventorySize;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.punishment.Punishment;
import net.aeronetwork.core.redis.impl.StaffMessageListener;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.ItemStackBuilder;
import net.aeronetwork.core.util.UpdateType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class PunishmentHistoryMenu extends AeroInventory {

    @Getter
    private AeroPlayer player;

    public PunishmentHistoryMenu(AeroPlayer player) {
        super("§e" + player.getAccountName() + "'s history", InventorySize.SIX_ROWS, null);
        this.player = player;
    }

    public void load() {
        for(Punishment punishment : this.player.getPunishments()) {
            List<String> brokenId = Lists.newArrayList();
            String currentId = "";
            if(punishment.getPunishmentId() != null) {
                for(char c : punishment.getPunishmentId().toCharArray()) {
                    if(currentId.length() <= punishment.getPunishmentId().length() / 2) {
                        currentId += c;
                    } else {
                        brokenId.add(currentId);
                        currentId = String.valueOf(c);
                    }
                }
            } else {
                brokenId.add("UNKNOWN");
            }
            brokenId.add(currentId);

            ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER, ItemStackBuilder.ItemType.NORMAL)
                    .setName("§4" + StringUtils.capitalize(punishment.getType().name().toLowerCase().replace("_", " ")) + " §8- §" + (punishment.isValid() ? "a§lActive" : "c§lExpired"))
                    .setAmount(1)
                    .setLore(Lists.newArrayList(
                            "§8ID: " + brokenId.get(0),
                            "§8" + brokenId.get(1),
                            "§cIssuer: §e" + AeroCore.PLAYER_MANAGER.getPlayer(punishment.getIssuer()).getAccountName(),
                            "§cReason: §e" + punishment.getReason(), "§cActive: §e" + punishment.isValid(),
                            "§cPunished On: §e" + AeroCore.PUNISHMENT_MANAGER.dateFormat.format(punishment.getPunishedOn()),
                            "§cEnds: §e" + (punishment.isPermanent() ? "Never" : punishment.getLength() == -1 ? "Never" : AeroCore.PUNISHMENT_MANAGER.dateFormat.format(punishment.getPunishedOn() + punishment.getLength())),
                            "§cForce Expired: §e" + punishment.isForceExpired(),
                            " ",
                            punishment.isValid() ? "§eClick to force expire this punishment." : "§cThis punishment is no longer valid."
                    )
            );

            if(punishment.isValid()) {
                builder.build().setGlowing();
            }

            addItem(
                    builder.get(),
                    data -> {
                        if(punishment.isValid()) {
                            punishment.setForceExpired(true);
                            data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.LEVEL_UP, 5, 1);
                            this.player.updatePunishment(punishment, UpdateType.UPDATE);
                            AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent(
                                    StaffMessageListener.CHANNEL,
                                    FM.mainFormat("Punish", "§c" +
                                            data.getPlayer().getName() +
                                            " §eforce expired a §c" + StringUtils.capitalize(punishment.getType().getSubType().name().toLowerCase())
                                            + " §efor §c" + player.getAccountName() + "§e. §7§o(ID: " + punishment.getPunishmentId().split("-")[0] + ")")
                            ));
                            data.getPlayer().sendMessage(FM.mainFormat("Punish", "You force expired a punishment for §c" + player.getAccountName()));
                            data.getPlayer().closeInventory();
                        }
                    }
            );
        }

        if(getItems().size() == 0) {
            addItem(
                    new ItemStackBuilder(Material.CAULDRON_ITEM, ItemStackBuilder.ItemType.NORMAL)
                            .setName("§cNo Punishments!")
                            .setLore(Lists.newArrayList(
                                    " ",
                                    "§eThis player has no",
                                    "§epunishments!"
                            ))
                            .get(),
                    22,
                    data ->
                        data.getPlayer().playSound(data.getPlayer().getLocation(), Sound.VILLAGER_NO, 5, 1)
            );
        }
    }

    @Override
    public void onClick(ClickData data) {
    }

    @Override
    public void onClose(Player player) {

    }
}
