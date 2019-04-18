package net.aeronetwork.core.command.impl.management;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.CommandHelper;

public class ServerInfoCommand extends Command {

    public ServerInfoCommand() {
        super("serverinfo", "Retrieve server info.", "/serverinfo", null);
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        CommandHelper.replyToSender(player, "§eServer Details:");
        AeroCore.SERVER_MANAGER.getServerEnvironment().getEnvVariables().forEach((var, val) ->
                CommandHelper.replyToSender(player, "§a" + var + " = " + val));
        CommandHelper.replyToSender(player, "§5Private Mode: " +
                AeroCore.SERVER_MANAGER.getServerSettings().isPrivateMode());
        CommandHelper.replyToSender(player, "§4Disable Stat Tracking: " +
                AeroCore.SERVER_MANAGER.getServerSettings().isDisableStatTracking());
        CommandHelper.replyToSender(player, "§bOffline Mode: " +
                AeroCore.SERVER_MANAGER.getServerSettings().isOfflineMode());
        CommandHelper.replyToSender(player, "§2Max Players: " +
                AeroCore.SERVER_MANAGER.getServerSettings().getMaxPlayers());
        CommandHelper.replyToSender(player, "§6Join State: §6§l" +
                AeroCore.SERVER_MANAGER.getServerSettings().getJoinState().name());
        CommandHelper.replyToSender(player, "§3MOTD: " +
                AeroCore.SERVER_MANAGER.getServerSettings().getMotd());
    }
}
