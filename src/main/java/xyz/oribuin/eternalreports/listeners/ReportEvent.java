package xyz.oribuin.eternalreports.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.data.PlayerData;
import xyz.oribuin.eternalreports.events.PlayerReportEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportEvent implements Listener {

    private final EternalReports plugin;
    private int columnCount;

    public ReportEvent(EternalReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerReport(PlayerReportEvent event) {
        PlayerData reportedData = new PlayerData(plugin, event.getPlayer());
        PlayerData senderData = new PlayerData(plugin, event.getSender());

        plugin.getDataManager().updateReports(this.getColumnCount() + 1, event.getPlayer(), event.getSender(), event.getTitle(), event.getDescription(), false);
        plugin.getDataManager().updatePlayer(event.getPlayer(), reportedData.getReports(), reportedData.getReported());
        plugin.getDataManager().updatePlayer(event.getSender(), senderData.getReports(), senderData.getReported());
    }


    private int getColumnCount() {
        this.plugin.getConnector().connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "reports")) {
                ResultSet resultSet = statement.executeQuery();
                resultSet.last();
                columnCount = resultSet.getRow();
            }
        });

        return columnCount;
    }

    private String getTablePrefix() {
        return plugin.getDescription().getName().toLowerCase() + "_";
    }
}
