package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.network.NetworkManager;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.network.NetworkPlayer;
import net.aeronetwork.core.redis.listener.ListenerComponent;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.Util;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class MessageCommand extends Command {

    public MessageCommand() {
        super("message", "Message a player.", "/msg <player> <message>", Arrays.asList("msg", "tell", "whisper"));
        setPlayerOnly(true);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(details.getArgs().length <= 1) {
            player.sendMessage(FM.mainFormat("Message", "Usage: /msg <player> <message>"));
        } else {
            if(AeroCore.PUNISHMENT_MANAGER.isMuted(player.getUuid())) {
                player.sendMessage(FM.mainFormat("Message", "§cYou cannot message while muted!"));
                return;
            }

            NetworkPlayer target = NetworkManager.getInstance().getPlayer(details.getArgs()[0]).getNow(null);

            if(target != null) {
                AeroPlayer aeroTarget = AeroCore.PLAYER_MANAGER.getPlayer(target.getUuid());

                // Make sure you can't message a disguised player or vanished player
                if(player.getRank().getPriority() > aeroTarget.getRank().getPriority() && (aeroTarget.isDisguised() || aeroTarget.isVanished())) {
                    player.sendMessage(FM.mainFormat("Message", "That player isn't on the network."));
                    return;
                }

                String message = Util.join(1, details.getArgs());
                String playerName = player.isDisguised() ? player.getDisguiseData().getName() : player.getAccountName();
                ChatColor playerColor = player.isDisguised() ? player.getDisguiseData().getRank().getColor() : player.getRank().getColor();

                String targetName = aeroTarget.isDisguised() ? aeroTarget.getDisguiseData().getName() : aeroTarget.getAccountName();
                ChatColor targetColor = aeroTarget.isDisguised() ? aeroTarget.getDisguiseData().getRank().getColor() : aeroTarget.getRank().getColor();

                AeroCore.REDIS_MANAGER.sendListenerMessage(new ListenerComponent("player_message", target.getUuid() + " §cFrom " + playerColor + playerName + " §8» §e" + message));
                player.sendMessage("§cTo " + targetColor + targetName + " §8» §e" + message);
            } else {
                // Let's assume the NetworkManaget failed. ):!
                AeroPlayer newTarget = AeroCore.PLAYER_MANAGER.getPlayerCache().asMap().values().stream().filter(aeroPlayer -> aeroPlayer.getAccountName().equalsIgnoreCase(details.getArgs()[0]) || (aeroPlayer.isDisguised() && aeroPlayer.getDisguiseData().getName().equalsIgnoreCase(details.getArgs()[0]))).findFirst().orElse(null);

                if(newTarget != null) {
                    if(player.getRank().getPriority() > newTarget.getRank().getPriority() && newTarget.isVanished()) {
                        player.sendMessage(FM.mainFormat("Message", "That player isn't on the network."));
                        return;
                    }

                    String message = Util.join(1, details.getArgs());
                    String playerName = player.isDisguised() ? player.getDisguiseData().getName() : player.getAccountName();
                    ChatColor playerColor = player.isDisguised() ? player.getDisguiseData().getRank().getColor() : player.getRank().getColor();

                    String targetName = newTarget.isDisguised() ? newTarget.getDisguiseData().getName() : newTarget.getAccountName();
                    ChatColor targetColor = newTarget.isDisguised() ? newTarget.getDisguiseData().getRank().getColor() : newTarget.getRank().getColor();

                    newTarget.sendMessage("§cFrom " + playerColor + playerName + " §8» §e" + message);
                    player.sendMessage("§cTo " + targetColor + targetName + " §8» §e" + message);
                } else {
                    player.sendMessage(FM.mainFormat("Message", "That player isn't on the network."));
                }
            }
        }
    }

}
