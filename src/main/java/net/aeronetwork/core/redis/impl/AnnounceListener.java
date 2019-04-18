package net.aeronetwork.core.redis.impl;

import net.aeronetwork.core.redis.listener.RedisMessageListener;
import net.aeronetwork.core.util.FM;
import net.aeronetwork.core.util.nms.PacketUtil;
import org.bukkit.Bukkit;

public class AnnounceListener extends RedisMessageListener {

    public AnnounceListener() {
        super("announce");
    }

    @Override
    public void onReceive(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(FM.mainFormat("Announcement", message));
            PacketUtil.sendTitle(player, "§c§lAnnouncement", "§e" + message, 10, 20 * 5, 10);
        });
    }
}
