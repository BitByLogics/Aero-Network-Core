package net.aeronetwork.core.map.impl;

import com.google.common.base.Joiner;
import net.aeronetwork.core.database.sql.SQLConnection;
import net.aeronetwork.core.map.Map;
import net.aeronetwork.core.map.MapManager;
import net.aeronetwork.core.map.data.MapData;
import net.aeronetwork.core.map.data.MapInfo;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Basic implementation of {@link MapManager}, with extra methods for easier
 * world management.
 */
public class DefaultMapManager extends MapManager {

    private String gameType;

    public DefaultMapManager(SQLConnection connection, String gameType) {
        super(connection);
        this.gameType = gameType;
    }

    public void loadAllMaps() {
        if(this.gameType != null)
            super.loadAllMaps(this.gameType);
    }

    public void loadAllMaps(String modeType) {

    }

    public <T extends Map> CompletableFuture<MapData<T>> loadRandomMap(String worldName, Class<T> dataClass) {
        if(getMapCache().size() >= 1) {
            Random r = new Random();
            MapInfo randomInfo = getMapCache().get(r.nextInt(getMapCache().size()));
            return super.load(getServerRootFile(), worldName, randomInfo.getId(), dataClass);
        }

        return null;
    }

    public World loadWorld(MapData data) {
        try {
            // Must delete the uid.dat for the world to load
            File uid = new File(data.getMapFolder().getAbsolutePath() + File.separatorChar + "uid.dat");
            if(uid.exists())
                FileUtils.forceDelete(uid);
            return Bukkit.getServer().createWorld(new WorldCreator(data.getMapFolder().getName()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getServerRootFile() {
        String[] pathArray = Bukkit.getServer().getWorlds().get(0)
                .getWorldFolder().getAbsolutePath().split(File.separatorChar + "");
        return new File(Joiner.on(File.separatorChar).join(Arrays.copyOf(pathArray, pathArray.length - 1)));
    }
}
