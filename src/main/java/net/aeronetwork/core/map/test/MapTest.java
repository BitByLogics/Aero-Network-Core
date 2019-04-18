package net.aeronetwork.core.map.test;

import com.google.common.base.Joiner;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.database.sql.SQLConnection;
import net.aeronetwork.core.map.impl.DefaultMap;
import net.aeronetwork.core.map.impl.DefaultMapManager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Arrays;

public class MapTest extends Command {

    public static DefaultMapManager MANAGER = new DefaultMapManager(new SQLConnection("AeroNetwork",
            "127.0.0.1", 3306, "root", "example_password"), "TEST");

    public MapTest() {
        super("maptest", "",
                "/maptest <save|load> <worldName|newWorldName> <id> [game_type] [mode_type] OR " +
                        "/maptest <zsave|zload> <zip|zipName> <id>", null);

        setPlayerOnly(true);
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length >= 1) {
            String[] args = details.getArgs();

            if(args[0].equalsIgnoreCase("save") && args.length >= 5) {
                MANAGER.save(Bukkit.getServer().getWorld(args[1]).getWorldFolder(), args[2], args[3], args[4]);
                player.sendMessage("§aSAVED!");
            } else if(args[0].equalsIgnoreCase("load") && args.length >= 3) {
                String[] pathArray = Bukkit.getServer().getWorlds().get(0).getWorldFolder().getAbsolutePath().split(File.separatorChar + "");
                String root = Joiner.on(File.separatorChar).join(Arrays.copyOf(pathArray, pathArray.length - 1));
                MANAGER.load(new File(root), args[1], args[2], DefaultMap.class).thenAccept(data -> {
                    player.getBukkitPlayer().sendMessage(data.getData().getName());
                    player.getBukkitPlayer().sendMessage(Arrays.toString(data.getData().getBuilders().toArray()));
                    World world = MANAGER.loadWorld(data);
                    if(world != null)
                        player.getBukkitPlayer().teleport(world.getSpawnLocation());
                });
            } else if(args[0].equalsIgnoreCase("zsave") && args.length >= 3) {
                String[] pathArray = Bukkit.getServer().getWorlds().get(0).getWorldFolder().getAbsolutePath().split(File.separatorChar + "");
                String root = Joiner.on(File.separatorChar).join(Arrays.copyOf(pathArray, pathArray.length - 1));
//                MANAGER.saveZip(new File(root), args[1], args[2]);
                player.sendMessage("§aSAVED!");
            } else if(args[0].equalsIgnoreCase("zload") && args.length >= 3) {
                String[] pathArray = Bukkit.getServer().getWorlds().get(0).getWorldFolder().getAbsolutePath().split(File.separatorChar + "");
                String root = Joiner.on(File.separatorChar).join(Arrays.copyOf(pathArray, pathArray.length - 1));
//                MANAGER.loadZip(new File(root), args[1], args[2]);
            } else {
                player.sendMessage("§c" + getUsage());
            }
        } else {
            player.sendMessage("§c" + getUsage());
        }
    }
}
