package xyz.oribuin.eternalreports.database;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Esophose
 */
public class SQLiteConnector implements DatabaseConnector {

    private final Plugin plugin;
    private final String connectionString;
    private Connection connection;

    public SQLiteConnector(Plugin plugin) {
        this.plugin = plugin;
        File dbFile = new File(plugin.getDataFolder(), plugin.getDescription().getName().toLowerCase() + ".db");
        this.connectionString = "jdbc:sqlite:" + dbFile.getPath();

        try {
            if (dbFile.exists())
                dbFile.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isInitialized() {
        return true; // Always available
    }

    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            this.plugin.getLogger().severe("An error occurred closing the SQLite database connection: " + ex.getMessage());
        }
    }

    public void connect(ConnectionCallback callback) {
        if (this.connection == null) {
            try {
                this.connection = DriverManager.getConnection(this.connectionString);
            } catch (SQLException ex) {
                this.plugin.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
            }
        }

        try {
            callback.accept(this.connection);
        } catch (Exception ex) {
            this.plugin.getLogger().severe("An error occurred executing an SQLite query: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
