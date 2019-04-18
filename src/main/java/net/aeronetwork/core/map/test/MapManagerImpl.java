package net.aeronetwork.core.map.test;

import net.aeronetwork.core.database.sql.SQLConnection;
import net.aeronetwork.core.map.MapManager;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapManagerImpl extends MapManager {

    public MapManagerImpl(SQLConnection connection) {
        super(connection);
    }

    public void saveZip(File root, String zip, String id) {
        File zipFile = new File(root.getAbsolutePath() + "/" + zip);
        if(!zipFile.exists())
            return;

        try (Connection c = super.getConnection().getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO map_data (id, map_file) VALUES (?, ?);");
            ps.setString(1, id);
            ps.setBinaryStream(2, new FileInputStream(zipFile));
            ps.executeUpdate();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadZip(File root, String zipName, String id) {
        InputStream in = null;
        try (Connection c = super.getConnection().getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT map_file FROM map_data WHERE id = ?");
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                in = rs.getBinaryStream("map_file");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(in != null) {
            try {
                FileUtils.copyInputStreamToFile(in,
                        new File(root.getAbsolutePath() + File.separatorChar + zipName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
