package xyz.oribuin.eternalreports.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.events.PlayerReportEvent;

public class ReportEvent implements Listener {

    private final EternalReports plugin;

    public ReportEvent(EternalReports plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerReport(PlayerReportEvent event) {
        plugin.getDataManager().updateReports(event.getPlayer(), event.getSender(), event.getTitle(), event.getDescription(), false);
    }

}
