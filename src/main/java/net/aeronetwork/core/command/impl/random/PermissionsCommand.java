package net.aeronetwork.core.command.impl.random;

import com.google.common.collect.Lists;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;

import java.util.List;

public class PermissionsCommand extends Command {

    public PermissionsCommand() {
        super("myperms", "View your own permissions", "/myperms", null);
        //setRank(Rank.ADMIN);
        setPlayerOnly(true);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        List<String> permissions = Lists.newArrayList();
        player.getBukkitPlayer().getEffectivePermissions().forEach(permission -> permissions.add(permission.getPermission()));

        player.sendMessage("§ePermissions: §8(§c" + String.join("§8, §c", permissions) + "§8)");
    }
}
