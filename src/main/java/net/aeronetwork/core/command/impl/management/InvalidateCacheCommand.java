package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;

public class InvalidateCacheCommand extends Command {

    public InvalidateCacheCommand() {
        super("invalidatecache", "Invalidates player cache.", "/invalidatecache", null);
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        AeroCore.PLAYER_MANAGER.getPlayerCache().invalidateAll();

        player.sendMessage("§aInvalidated cache. The next time a player requests their player data, " +
                "a new object will be retrieved.");
    }
}
