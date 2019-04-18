package net.aeronetwork.core.command.impl.management;

import com.google.common.base.Joiner;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Arrays;

public class ListAllRootFilesCommand extends Command {

    public ListAllRootFilesCommand() {
        super("listallrootfiles", "", "", null);

        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        File file = Bukkit.getServer().getWorlds().get(0).getWorldFolder();
        String[] args = file.getAbsolutePath().split(File.separatorChar + "");

        File root = new File(Joiner.on(File.separatorChar).join(Arrays.copyOfRange(args, 0, args.length - 1)));

        if(root.exists() && root.isDirectory() && root.listFiles() != null) {
            for(File f : root.listFiles()) {
                player.getBukkitPlayer().sendMessage(f.getName());
            }
        } else {
            player.sendMessage("§cInvalid root!");
        }
    }
}
