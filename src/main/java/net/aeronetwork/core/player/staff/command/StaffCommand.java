package net.aeronetwork.core.player.staff.command;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;

import java.util.Arrays;

public class StaffCommand extends Command {

    public StaffCommand() {
        super("staff", "Toggle staff mode", "/staff", Arrays.asList("mod", "staffmode", "modmode"));
        setPlayerOnly(true);
        setRank(Rank.JR_STAFF);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        AeroCore.STAFF_MANAGER.toggleStaffMode(player);
    }
}
