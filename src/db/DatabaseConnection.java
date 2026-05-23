package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:drone_system.db";

    public DatabaseConnection() {

    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        Connection conn = DriverManager.getConnection(URL);
        return conn;
    }
}
