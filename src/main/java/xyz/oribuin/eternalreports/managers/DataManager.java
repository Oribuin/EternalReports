package xyz.oribuin.eternalreports.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.utils.FileUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataManager extends Manager {

    private int reportSize;

    public DataManager(EternalReports plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        FileUtils.createFile(plugin, plugin.getDescription().getName().toLowerCase() + ".db");
        this.createTables();
    }

    // Create all the SQLite Tables for first startup
    private void createTables() {

        String[] queries = {
                "CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "reports (reported TXT, sender TXT, reason TXT, resolved BOOLEAN, PRIMARY KEY(reported, sender))",
                "CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "players (uuid TXT, reports INT, reported INT, PRIMARY KEY (uuid))"
        };

        // Run queries as async
        this.async(() -> this.plugin.getConnector().connect(connection -> {
            for (String string : queries) {
                try (PreparedStatement statement = connection.prepareStatement(string)) {
                    statement.executeUpdate();
                }
            }
        }));
    }

    public void updateReports(Player reported, Player sender, String reason, boolean resolved) {
        this.async(() -> this.plugin.getConnector().connect(connection -> {
            String updateUser = "REPLACE INTO " + this.getTablePrefix() + "reports (reported, sender, reason, resolved) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(updateUser)) {
                statement.setString(1, reported.getUniqueId().toString());
                statement.setString(2, sender.getUniqueId().toString());
                statement.setString(3, reason);
                statement.setBoolean(4, resolved);
                statement.executeUpdate();
            }
        }));
    }


    public int getReportSize() {
        plugin.getConnector().connect(connection -> {
            String query = "SELECT COUNT (*) FROM " + this.getTablePrefix() + "reports";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet result = statement.executeQuery();
                result.next();
                reportSize = result.getInt(1);
            }
        });

        return reportSize;
    }

    private void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncCallback);
    }

    private String getTablePrefix() {
        return plugin.getDescription().getName().toLowerCase() + "_";
    }


}
