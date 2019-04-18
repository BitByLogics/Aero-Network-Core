package net.aeronetwork.core.command.impl.player.disguise;

import com.google.common.collect.Lists;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DisguiseCommand extends Command {

    public DisguiseCommand() {
        super("disguise", "Disguise and hide from nons.", "/disguise <name> <skin> <rank>", Lists.newArrayList("nick", "dis"));
        setRank(Rank.YT);
        setPlayerOnly(true);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());

        if(player.isDisguised()) {
            player.sendMessage(FM.mainFormat("Disguise", "You're already disguised."));
            return;
        }

        if(details.getArgs().length == 0) {
            bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "Usage: &c" + getUsage()));
        }

        if(details.getArgs().length == 1) {
            if(details.getArgs()[0].length() > 16) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, disguise names can only be 16 character or less."));
                return;
            }

            AeroCore.DISGUISE_MANAGER.disguise(player, details.getArgs()[0], details.getArgs()[0], Rank.DEFAULT);
        }

        if(details.getArgs().length == 2) {
            if(details.getArgs()[0].length() > 16) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, disguise names can only be 16 character or less."));
                return;
            }

            if(details.getArgs()[1].length() > 16) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, disguise skins can only be 16 character or less."));
                return;
            }

            AeroCore.DISGUISE_MANAGER.disguise(player, details.getArgs()[0], details.getArgs()[1], Rank.DEFAULT);
        }

        if(details.getArgs().length >= 3) {
            if(details.getArgs()[0].length() > 16) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, disguise names can only be 16 character or less."));
                return;
            }

            if(details.getArgs()[1].length() > 16) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, disguise skins can only be 16 character or less."));
                return;
            }

            if(Rank.match(details.getArgs()[2]) == null) {
                bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "&cSorry, please provide a valid rank."));
                return;
            }

            AeroCore.DISGUISE_MANAGER.disguise(player, details.getArgs()[0], details.getArgs()[1], Rank.match(details.getArgs()[2]));
        }
    }
}
