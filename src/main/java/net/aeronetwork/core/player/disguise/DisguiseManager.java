package net.aeronetwork.core.player.disguise;

import com.nametagedit.plugin.NametagEdit;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.manager.Manager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.command.impl.player.disguise.DisguiseCommand;
import net.aeronetwork.core.command.impl.player.disguise.UndisguiseCommand;
import net.aeronetwork.core.player.disguise.utils.DisguiseUtils;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.command.impl.management.RanksCommand;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DisguiseManager extends Manager {

    public DisguiseManager(JavaPlugin plugin) {
        super("Disguise Manager", "Handles all disguise related tasks.", plugin);
        AeroCore.COMMAND_MANAGER.registerCommand(DisguiseCommand.class);
        AeroCore.COMMAND_MANAGER.registerCommand(UndisguiseCommand.class);
        AeroCore.COMMAND_MANAGER.registerCommand(RanksCommand.class);
    }

    /**
     * Disguise the specified player.
     *
     * @param player The player being disguised.
     * @param name The name they're being disguised as.
     * @param skin The skin they're being disguised with.
     * @param rank The rank they're being disguised with.
     */
    public void disguise(AeroPlayer player, String name, String skin, Rank rank) {
        if(Bukkit.getPlayerExact(name) != null) {
            player.sendMessage(FM.mainFormat("Disguise", "&cYou cannot disguise as an online player."));
            return;
        }

        if(AeroCore.PLAYER_MANAGER.getPlayer(name) != null
                && AeroCore.PLAYER_MANAGER.getPlayer(name).getRank().getPriority() < player.getRank().getPriority()) {
            player.sendMessage(FM.mainFormat("Disguise", "You cannot disguise as someone with a higher rank than you."));
            return;
        }

        if(rank.getPriority() < player.getRank().getPriority()) {
            player.sendMessage(FM.mainFormat("Disguise", "You cannot disguise with a rank higher than yours."));
            return;
        }

        Player bukkitPlayer = player.getBukkitPlayer();
        player.updateDisguised(true);
        player.updateDisguiseData(new DisguiseData(name, skin, rank));

        DisguiseUtils.modifyGameProfile(name, skin, ((CraftPlayer) bukkitPlayer).getHandle().getProfile());
        DisguiseUtils.updateSelf(bukkitPlayer);
        DisguiseUtils.updateAll(bukkitPlayer);
        NametagEdit.getApi().clearNametag(bukkitPlayer.getName());
        NametagEdit.getApi().setNametag(
                bukkitPlayer.getName(),
                player.getDisguiseData().getRank().getPrefix(),
                player.getDisguiseData().getRank().getSuffix()
        );
        bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "Now disguised as: &c" + name));
    }

    /**
     * Undisguise the specified player.
     *
     * @param p The player who is being undisguised.
     */
    public void undisguise(AeroPlayer p) {
        Player bukkitPlayer = Bukkit.getPlayer(p.getUuid());
        p.updateDisguised(false);
        DisguiseUtils.modifyGameProfile(
                p.getAccountName(),
                p.getAccountName(),
                ((CraftPlayer) bukkitPlayer).getProfile()
        );
        DisguiseUtils.updateSelf(bukkitPlayer);
        DisguiseUtils.updateAll(bukkitPlayer);
        NametagEdit.getApi().setNametag(bukkitPlayer, p.getRank().getPrefix(), p.getRank().getSuffix());
        bukkitPlayer.sendMessage(FM.mainFormat("Disguise", "You're no longer disguised."));
    }

    /**
     * Used for redisguising players on login.
     *
     * @param player The player whose being redisguised.
     */
    public void redisguise(AeroPlayer player) {
        if(player.isDisguised()) {
            if(player.getRank().getPriority() <= Rank.YT.getPriority()) {
                if(Bukkit.getPlayer(player.getDisguiseData().getName()) != null) {
                    player.sendMessage(FM.mainFormat("Disguise", "&cYour disguise is an online player, undisguising."));
                    player.updateDisguised(false);
                } else if(AeroCore.PLAYER_MANAGER.getPlayer(player.getDisguiseData().getName()) != null
                        && AeroCore.PLAYER_MANAGER.getPlayer(player.getDisguiseData().getName()).getRank().getPriority() < player.getRank().getPriority()) {
                    player.sendMessage(FM.mainFormat("Disguise", "&cThe player you were disguised as has a higher rank than you, undisguising."));
                    player.updateDisguised(false);
                    return;
                } else if(player.getDisguiseData().getRank().getPriority() < player.getRank().getPriority()) {
                    player.sendMessage(FM.mainFormat("Disguise", "&cYour disguised rank is higher than your rank, undisguising."));
                    player.updateDisguised(false);
                } else {
                    player.sendMessage(FM.mainFormat("Disguise", "Logging in disguised as §c" + player.getDisguiseData().getName()));
                    DisguiseUtils.updatePlayer(player.getBukkitPlayer(), player.getDisguiseData());
                }
            } else {
                player.updateDisguised(false);
                player.sendMessage(FM.mainFormat("Disguise", "You no longer have permission to disguise, you're now undisguised."));
            }
        }
    }
}
