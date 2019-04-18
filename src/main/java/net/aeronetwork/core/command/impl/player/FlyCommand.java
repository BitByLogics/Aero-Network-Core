package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;

public class FlyCommand extends Command {

    public FlyCommand() {
        super("fly", "Toggle flight.", "/fly", null);
        setPlayerOnly(true);
        setAllowedInGame(false);
        setRank(Rank.YT);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player.getBukkitPlayer().getAllowFlight() || player.getBukkitPlayer().isFlying()) {
            player.getBukkitPlayer().setAllowFlight(false);
            player.getBukkitPlayer().setFallDistance(-1);
            player.sendMessage(FM.mainFormat("Fly", "Flight has been disabled."));
        } else {
            player.getBukkitPlayer().setAllowFlight(true);
            player.sendMessage(FM.mainFormat("Fly", "Flight has been enabled."));
        }
    }

}
