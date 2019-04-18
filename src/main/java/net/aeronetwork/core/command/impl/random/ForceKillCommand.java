package net.aeronetwork.core.command.impl.random;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.util.FM;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;

public class ForceKillCommand extends Command {

    public ForceKillCommand() {
        super("forcekill", "Force kill all mobs of specified mob type.", "/forcekill <mob type> (radius)", Arrays.asList("fk"));
        setRank(Rank.ADMIN);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length == 0) {
            player.sendMessage(FM.mainFormat("Force Kill", "Usage: " + getUsage()));
        } else if(details.getArgs().length == 1) {
            if(Arrays.stream(EntityType.values()).anyMatch(type -> type.name().equalsIgnoreCase(details.getArgs()[0]))) {
                EntityType type = EntityType.valueOf(details.getArgs()[0].toUpperCase());
                if(type.equals(EntityType.PLAYER) || type.equals(EntityType.COMPLEX_PART) || type.equals(EntityType.UNKNOWN)) {
                    player.sendMessage(FM.mainFormat("Force Kill", "You cannot kill that entity type."));
                    return;
                } else {
                    player.getBukkitPlayer().getWorld().getEntitiesByClass(type.getEntityClass()).forEach(Entity::remove);
                    player.sendMessage(FM.mainFormat("Force Kill", "Removed all &c" + StringUtils.capitalize(type.name().replace("_", " ").toLowerCase()) + " &eentities."));
                }
            } else {
                player.sendMessage(FM.mainFormat("Force Kill", "Invalid entity type."));
            }
        } else {
            if(Arrays.stream(EntityType.values()).anyMatch(type -> type.name().equalsIgnoreCase(details.getArgs()[0]))) {
                EntityType type = EntityType.valueOf(details.getArgs()[0].toUpperCase());
                if(type.equals(EntityType.PLAYER) || type.equals(EntityType.COMPLEX_PART) || type.equals(EntityType.UNKNOWN)) {
                    player.sendMessage(FM.mainFormat("Force Kill", "You cannot kill that entity type."));
                    return;
                } else {
                    if(NumberUtils.isNumber(details.getArgs()[1])) {
                        int radius = Integer.valueOf(details.getArgs()[1]);
                        if(radius <= 500) {
                            player.getBukkitPlayer().getWorld().getNearbyEntities(player.getBukkitPlayer().getLocation(), radius, radius, radius).forEach(entity -> {if(entity.getType().equals(type)) {entity.remove();}});
                            player.sendMessage(FM.mainFormat("Force Kill", "Removed all &c" + StringUtils.capitalize(type.name().replace("_", " ").toLowerCase()) + " &eentities in a " + radius + " block radius."));
                        } else {
                            player.sendMessage(FM.mainFormat("Force Kill", "Radius cannot be bigger than 500."));
                        }
                    } else {
                        player.sendMessage(FM.mainFormat("Force Kill", "Invalid radius."));
                    }
                }
            } else {
                player.sendMessage(FM.mainFormat("Force Kill", "Invalid entity type."));
            }
        }
    }
}
