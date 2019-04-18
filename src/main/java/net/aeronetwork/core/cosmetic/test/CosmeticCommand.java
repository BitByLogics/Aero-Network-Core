package net.aeronetwork.core.cosmetic.test;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.cosmetic.CosmeticManager;
import net.aeronetwork.core.cosmetic.menu.CosmeticCategoryMenu;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;

public class CosmeticCommand extends Command {

    public static CosmeticManager MANAGER = new CosmeticManagerImpl();

    public CosmeticCommand() {
        super("cosmetic", "", "", null);

        setPlayerOnly(true);
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        new CosmeticCategoryMenu(MANAGER).openInventory(player.getBukkitPlayer());
    }
}
