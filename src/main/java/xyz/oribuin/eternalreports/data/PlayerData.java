package xyz.oribuin.eternalreports.data;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalreports.EternalReports;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerData {

    private final EternalReports plugin;
    private final Player player;
    private int reports;
    private int reported;


    public PlayerData(EternalReports plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public int getReports() {
        this.plugin.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT reports FROM " + this.getTablePrefix() + "players WHERE uuid = ?")) {
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next())
                    return;

                reports = resultSet.getInt(1);
            }
        });

        return reports;
    }

    public int getReported() {
        this.plugin.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT reported FROM " + this.getTablePrefix() + "players WHERE uuid = ?")) {
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next())
                    return;

                reported = resultSet.getInt(1);
            }
        });

        return reported;
    }

    private String getTablePrefix() {
        return plugin.getDescription().getName().toLowerCase() + "_";
    }
}
