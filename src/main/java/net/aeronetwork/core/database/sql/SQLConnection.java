package net.aeronetwork.core.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

    private String host;
    private int port;
    private String database;

    private String userName;
    private String password;

    public SQLConnection(String database, String host, int port, String userName, String password) {
        this.database = database;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Non existent MySQL driver!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
                    this.userName,
                    this.password
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
