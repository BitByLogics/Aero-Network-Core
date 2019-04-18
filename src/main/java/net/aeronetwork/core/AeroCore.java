package net.aeronetwork.core;

import net.aeronetwork.core.command.CommandManager;
import net.aeronetwork.core.command.impl.management.*;
import net.aeronetwork.core.command.impl.player.*;
import net.aeronetwork.core.command.impl.player.punish.*;
import net.aeronetwork.core.command.impl.random.AnnounceCommand;
import net.aeronetwork.core.command.impl.random.ForceKillCommand;
import net.aeronetwork.core.command.impl.random.PermissionsCommand;
import net.aeronetwork.core.command.impl.random.SkullCommand;
import net.aeronetwork.core.cosmetic.test.CosmeticCommand;
import net.aeronetwork.core.listener.ConnectionListener;
import net.aeronetwork.core.map.test.MapTest;
import net.aeronetwork.core.player.PlayerManager;
import net.aeronetwork.core.player.chat.ChatManager;
import net.aeronetwork.core.player.discord.DiscordManager;
import net.aeronetwork.core.player.disguise.DisguiseManager;
import net.aeronetwork.core.player.experience.ExpManager;
import net.aeronetwork.core.player.punishment.PunishmentManager;
import net.aeronetwork.core.player.rank.RankManager;
import net.aeronetwork.core.player.staff.StaffManager;
import net.aeronetwork.core.redis.RedisManager;
import net.aeronetwork.core.redis.impl.AnnounceListener;
import net.aeronetwork.core.redis.impl.DiscordListener;
import net.aeronetwork.core.redis.impl.PlayerMessageListener;
import net.aeronetwork.core.redis.impl.StaffMessageListener;
import net.aeronetwork.core.server.ServerManager;
import net.aeronetwork.core.server.commands.MOTDCommand;
import net.aeronetwork.core.util.hologram.HologramManager;
import net.aeronetwork.core.util.npc.NPCManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AeroCore extends JavaPlugin {

    public static AeroCore INSTANCE;
    public static ServerManager SERVER_MANAGER = new ServerManager();
    public static PlayerManager PLAYER_MANAGER = new PlayerManager();
    public static PunishmentManager PUNISHMENT_MANAGER;
    public static CommandManager COMMAND_MANAGER = new CommandManager();
    public static RedisManager REDIS_MANAGER = new RedisManager();

    public static HologramManager HOLOGRAM_MANAGER;
    public static NPCManager NPC_MANAGER;
    public static DisguiseManager DISGUISE_MANAGER;
    public static ExpManager EXPERIENCE_MANAGER = new ExpManager();
    public static StaffManager STAFF_MANAGER;
    public static DiscordManager DISCORD_MANAGER;
    public static RankManager RANK_MANAGER;

    @Override
    public void onEnable() {
        INSTANCE = this;
        SERVER_MANAGER.start();
        PUNISHMENT_MANAGER = new PunishmentManager();
        HOLOGRAM_MANAGER = new HologramManager();
        NPC_MANAGER = new NPCManager();
        DISGUISE_MANAGER = new DisguiseManager(this);
        STAFF_MANAGER = new StaffManager(this);
        DISCORD_MANAGER = new DiscordManager(this);
        RANK_MANAGER = new RankManager(this);
        new ChatManager(this);
        registerListeners();

        COMMAND_MANAGER.registerCommands(
                RankCommand.class,
                InvalidateCacheCommand.class,
                ExpCommand.class,
                CoinsCommand.class,
                CrystalsCommand.class,
                MyCoinsCommand.class,
                MyCrystalsCommand.class,
                HelpCommand.class,
                MyLevelCommand.class,
                MOTDCommand.class,
                PunishCommand.class,
                HistoryCommand.class,
                WarnCommand.class,
                MuteCommand.class,
                BanCommand.class,
                GamemodeCommand.class,
                ServerInfoCommand.class,
                NetworkCommand.class,
                JoinCommand.class,
                SkullCommand.class,
                FlyCommand.class,
                VanishCommand.class,
                StaffChatCommand.class,
                ForceKillCommand.class,
                DiscordLinkCommand.class,
                ListAllRootFilesCommand.class,
                CosmeticCommand.class,
                MapTest.class,
                PermissionsCommand.class,
                MessageCommand.class,
                AnnounceCommand.class
        );

        reloadConfig();

        REDIS_MANAGER.registerListener(new PlayerMessageListener());
        REDIS_MANAGER.registerListener(new StaffMessageListener());
        REDIS_MANAGER.registerListener(new DiscordListener());
        REDIS_MANAGER.registerListener(new AnnounceListener());
    }

    @Override
    public void onDisable() {
        SERVER_MANAGER.stop();
        // TODO: Make this not throw an error on disable. Commented out for now.
//        try {
//            Unirest.shutdown();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ConnectionListener(), this);
    }
}
