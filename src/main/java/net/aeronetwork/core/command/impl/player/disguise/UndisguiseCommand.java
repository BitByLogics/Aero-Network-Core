package net.aeronetwork.core.command.impl.player.disguise;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class UndisguiseCommand extends Command {

    public UndisguiseCommand() {
        super("undisguise", "Undisguise from your current disguise.", "/undisguise", Arrays.asList("undis", "unnick"));
        setPlayerOnly(true);
        setRank(Rank.YT);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player.isDisguised()) {
            AeroCore.DISGUISE_MANAGER.undisguise(player);
        } else {
            Bukkit.getPlayer(player.getUuid()).sendMessage(FM.mainFormat("Disguise", "You're not disguised."));
        }
    }
}
