package xyz.oribuin.eternalreports.managers;

import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.data.ReportPlayer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ReportManager extends Manager {

    private final Map<UUID, ReportPlayer> reportPlayers;

    public ReportManager(EternalReports plugin) {
        super(plugin);
        this.reportPlayers = new ConcurrentHashMap<>();
    }

    @Override
    public void reload() {
        this.reportPlayers.clear();
    }

    public Collection<ReportPlayer> getReportPlayers() {
        return this.reportPlayers.values();
    }

    public void addReportedPlayer(ReportPlayer player) {
        this.reportPlayers.put(player.getUUID(), player);
    }
}
