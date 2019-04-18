package net.aeronetwork.core.util.nms;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.aeronetwork.core.AeroCore;
import net.aeronetwork.core.event.packet.in.PacketInEntityUseEvent;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.player.rank.Rank;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutTabComplete;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketInterceptorUtil {

    private PacketInterceptorUtil() {
    }

    public static void inject(Player player) {
        AeroPlayer ap = AeroCore.PLAYER_MANAGER.getPlayer(player.getUniqueId());
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
                if(o instanceof PacketPlayInUseEntity) {
                    PacketPlayInUseEntity packet = (PacketPlayInUseEntity) o;

                    PacketInEntityUseEvent event = new PacketInEntityUseEvent(player, packet);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if(!event.isIntercept())
                        super.channelRead(channelHandlerContext, o);

                    return;
                }
                super.channelRead(channelHandlerContext, o);
            }
        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", ap.getAccountName(), handler);
    }

    public static void eject(Player player) {
        AeroPlayer ap = AeroCore.PLAYER_MANAGER.getPlayer(player.getUniqueId());
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(ap.getAccountName());
            return null;
        });
    }
}
