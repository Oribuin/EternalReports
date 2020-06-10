package xyz.oribuin.eternalreports.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.utils.FileUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataManager extends Manager {

    // Reports Column
    private Player reported;
    private Player sender;
    private String title;
    private String description;
    private boolean resolved;
    // Players Column
    private Player player;
    private int reportAmt;
    private int reportedAmt;
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
                "CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "reports (reported TXT, sender TXT, title TXT, description TXT, resolved BOOLEAN, id INT)",
                "CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "players (uuid TXT, reports INT, reported INT)"
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

    public void updateReports(int id, Player reported, Player sender, String title, String description, boolean resolved) {
        this.async(() -> this.plugin.getConnector().connect(connection -> {
            String replaceData = "REPLACE INTO " + this.getTablePrefix() + "reports (reported, sender, title, description, resolved, id INT) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(replaceData)) {
                statement.setString(1, reported.getUniqueId().toString());
                statement.setString(2, sender.getUniqueId().toString());
                statement.setString(3, title);
                statement.setString(4, description);
                statement.setBoolean(5, resolved);
                statement.setInt(6, id);
                statement.executeUpdate();
            }
        }));
    }

    public void updatePlayer(Player player, int reports, int reported) {
        this.async(() -> this.plugin.getConnector().connect(connection -> {
            String replaceData = "REPLACE INTO " + this.getTablePrefix() + "players (uuid, reports, reported) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(replaceData)) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, reports);
                statement.setInt(3, reported);
                statement.executeUpdate();
            }
        }));
    }

    public Player getReported(int id) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT reported FROM " + this.getTablePrefix() + "reports WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                reported = Bukkit.getPlayer(result.getString(1));
            }
        });

        return reported;
    }

    public Player getSender(int id) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT sender FROM " + this.getTablePrefix() + "reports WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                sender = Bukkit.getPlayer(result.getString(1));
            }
        });

        return sender;
    }

    public String getTitle(int id) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT title FROM " + this.getTablePrefix() + "reports WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                title = result.getString(1);
            }
        });

        return title;
    }

    public String getDescription(int id) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT description FROM " + this.getTablePrefix() + "reports WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                description = result.getString(1);
            }
        });

        return description;
    }

    public boolean isResolved(int id) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT resolved FROM " + this.getTablePrefix() + "reports WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                resolved = result.getBoolean(1);
            }
        });

        return resolved;
    }

    public int getReportAmt(Player player) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT reports FROM " + this.getTablePrefix() + "players WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getUniqueId().toString());
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                reportAmt = result.getInt(1);
            }
        });

        return reportAmt;
    }

    public int getReportedAmt(Player player) {
        this.plugin.getConnector().connect(connection -> {
            String query = "SELECT reported FROM " + this.getTablePrefix() + "players WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getUniqueId().toString());
                ResultSet result = statement.executeQuery();
                if (!result.next())
                    return;

                reportedAmt = result.getInt(1);
            }
        });

        return reportedAmt;
    }


    private void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncCallback);
    }

    private String getTablePrefix() {
        return plugin.getDescription().getName().toLowerCase() + "_";
    }


}
