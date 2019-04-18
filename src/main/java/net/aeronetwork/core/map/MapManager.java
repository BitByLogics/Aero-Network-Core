package net.aeronetwork.core.map;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.aeronetwork.core.database.sql.SQLConnection;
import net.aeronetwork.core.map.data.MapData;
import net.aeronetwork.core.map.data.MapInfo;
import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Beta
public abstract class MapManager {

    @Getter
    private SQLConnection connection;
    @Getter
    private List<MapInfo> mapCache;

    private final String TABLE_NAME = "map_data";
    private final String ID_KEY = "id";
    private final String GAME_TYPE_KEY = "game_type"; //TODO: change to server_type instead
    private final String MODE_TYPE_KEY = "mode_type"; // solo, teams, etc.
    private final String MAP_FILE_KEY = "map_file";
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            ID_KEY + " VARCHAR(256) NOT NULL PRIMARY KEY, " +
            GAME_TYPE_KEY + " VARCHAR(256) NOT NULL, " +
            MODE_TYPE_KEY + " VARCHAR(256) NOT NULL, " +
            MAP_FILE_KEY + " MEDIUMBLOB" +
            ");";

    public MapManager(SQLConnection connection) {
        this.connection = connection;
        this.mapCache = Lists.newCopyOnWriteArrayList();

        setup();
    }

    private void setup() {
        // Setting up the table
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Connection c = connection.getConnection()) {
                PreparedStatement ps = c.prepareStatement(CREATE_TABLE);
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadAllMaps(String gameType) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Connection c = connection.getConnection()) {
                // Change to ID_KEY, MODE_TYPE_KEY in the future so it has to select less
                PreparedStatement ps = c.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " +
                        GAME_TYPE_KEY + " = \"" + gameType + "\"");
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    this.mapCache.add(new MapInfo(rs.getString(ID_KEY), gameType, rs.getString(MODE_TYPE_KEY)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadAllMaps(String gameType, String modeType) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try (Connection c = connection.getConnection()) {
                PreparedStatement ps = c.prepareStatement("SELECT " + ID_KEY + " FROM " + TABLE_NAME + " WHERE " +
                        GAME_TYPE_KEY + "=\"" + gameType + "\" AND " +
                        MODE_TYPE_KEY + "=\"" + modeType + "\""
                );
                ResultSet rs = ps.executeQuery();

                while(rs.next()) {
                    this.mapCache.add(new MapInfo(rs.getString(ID_KEY), gameType, modeType));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public <T extends Map> CompletableFuture<MapData<T>> load(File rootDirectory, String worldName, String id, Class<T> dataClass) {
        CompletableFuture<MapData<T>> future = new CompletableFuture<>();
        if(rootDirectory.isDirectory()) {
            Executors.newSingleThreadExecutor().submit(() -> {
                InputStream in = null;
                try (Connection c = connection.getConnection()) {
                    PreparedStatement ps = c.prepareStatement("SELECT " + MAP_FILE_KEY +
                            " FROM " + TABLE_NAME + " WHERE " + ID_KEY + "=" + "\"" + id + "\"");
                    ResultSet rs = ps.executeQuery();

                    while(rs.next()) {
                        in = rs.getBinaryStream(MAP_FILE_KEY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(in != null) {
                    try {
                        File zipFile = new File(rootDirectory.getAbsolutePath() +
                                File.separatorChar + worldName + ".zip");
                        FileUtils.copyInputStreamToFile(in, zipFile);

                        // Unzip the file
                        File worldDirectory = new File(rootDirectory.getAbsolutePath() +
                                File.separatorChar + worldName);

                        ZipUtil.unpack(zipFile, worldDirectory);

                        File dataFile = new File(worldDirectory.getAbsolutePath() + File.separatorChar + "data.json");
                        if(!dataFile.exists())
                            throw new NullPointerException("data.json does not exist for map ID " + id);

                        Gson gson = new Gson();
                        T data = gson.fromJson(new FileReader(dataFile), dataClass);

                        future.complete(new MapData<>(worldDirectory, data));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return future;
    }

    public void save(File worldFolder, String id, String gameType, String modeType) {
        if(worldFolder.isDirectory()) {
            File dataFile = new File(worldFolder.getAbsolutePath() + File.separatorChar + "data.json");
            if(!dataFile.exists()) {
                generateDataFile(worldFolder);
                throw new UnsupportedOperationException("Could not find data.json in " + worldFolder.getAbsolutePath() +
                        ", default file has been created");
            }

            // Zipping the world
            String[] pathArray = worldFolder.getAbsolutePath().split(File.separatorChar + "");
            String root = Joiner.on(File.separatorChar).join(Arrays.copyOf(pathArray, pathArray.length - 1));

            File zipFile = new File(root + File.separatorChar + worldFolder.getName() + ".zip");

            if(zipFile.exists()) {
                try {
                    FileUtils.forceDelete(zipFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ZipUtil.pack(worldFolder, zipFile);

            // Uploading to the database
            Executors.newSingleThreadExecutor().submit(() -> {
                try (Connection c = connection.getConnection()) {
                    PreparedStatement ps = c.prepareStatement("INSERT INTO " + TABLE_NAME +
                            " (" + Joiner.on(", ").join(new String[] {ID_KEY, GAME_TYPE_KEY, MODE_TYPE_KEY, MAP_FILE_KEY}) +
                            ") VALUES (?, ?, ?, ?);");
                    ps.setString(1, id);
                    ps.setString(2, gameType);
                    ps.setString(3, modeType);
                    ps.setBinaryStream(4, new FileInputStream(zipFile));
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void generateDataFile(File worldFolder) {
        if(worldFolder.isDirectory()) {
            File data = new File(worldFolder.getAbsolutePath() + File.separatorChar + "data.json");

            GenericMapData mapData = new GenericMapData("MAP_NAME", Lists.newArrayList("Aero Network"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try {
                FileWriter writer = new FileWriter(data);
                gson.toJson(mapData, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AllArgsConstructor
    private class GenericMapData {

        private String name;
        private List<String> builders;

        public GenericMapData() {
        }
    }
}
