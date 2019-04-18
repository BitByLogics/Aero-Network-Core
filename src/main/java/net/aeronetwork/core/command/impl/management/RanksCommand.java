package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RanksCommand extends Command {

    public RanksCommand() {
        super("ranks", "View a list of ranks.", "/ranks", Arrays.asList("ranklist"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        //TODO: Remake this using JSON, currently no API for it.
        Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
        bukkitPlayer.sendMessage(FM.mainFormat("Rank", "Rank List: "));
        for (Rank rank : Rank.values()) {
            bukkitPlayer.sendMessage("§4§l" + rank.name() + String.format(" §8(§eFriendly Name: §c%s§8, §ePriority: §c%s§8, §eRank Type: §c%s§8, §eColor: §c%s§8, §ePrefix: §c%s§8, §eSuffix: §c%s§8)",
                    rank.getFriendlyName(), rank.getPriority(), rank.getRankType().name(), rank.getColor() + "Example", rank.getPrefix(), rank.getSuffix()));
        }
    }
}
