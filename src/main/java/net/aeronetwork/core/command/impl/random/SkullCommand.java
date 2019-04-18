package net.aeronetwork.core.command.impl.random;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class SkullCommand extends Command {

    public SkullCommand() {
        super("skull", "Get a player's skull.", "/skull <player>", Arrays.asList("head"));
        setPlayerOnly(true);
        setRank(Rank.BUILDER);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length <= 0) {
            player.sendMessage(FM.mainFormat("Skull", "Usage: /skull <player>"));
        } else {
            player.sendMessage(FM.mainFormat("Skull", "Recieved &c" + details.getArgs()[0] + "&e's skull."));
            Bukkit.getPlayer(player.getUuid()).getInventory().addItem(new ItemStackBuilder(Material.SKULL_ITEM, ItemStackBuilder.ItemType.SKULL).setOwner(details.getArgs()[0]).build().get());
        }
    }

}
