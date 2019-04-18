package net.aeronetwork.core.redis.impl;

import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.util.FM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffMessageListener extends RedisMessageListener {

    public static String CHANNEL = "staff_message";

    public StaffMessageListener() {
        super("staff_message");
    }

    @Override
    public void onReceive(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            AeroPlayer aeroPlayer = AeroCore.PLAYER_MANAGER.getPlayer(player.getUniqueId());
            if(aeroPlayer.getRank().getRankType() == Rank.RankType.HIGH_STAFF || aeroPlayer.getRank().getRankType() == Rank.RankType.STAFF) {
                player.sendMessage(FM.translate(message));
            }
        }
    }
}
