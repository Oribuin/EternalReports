package xyz.oribuin.eternalreports.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportPlayer {
    private final UUID uuid;
    private Player player;
    private List<Report> reports;
    private int usersReported;
    private int reportedAmount;

    public ReportPlayer(UUID uuid) {
        this.uuid = uuid;
        this.reports = new ArrayList<>();

        this.usersReported = 0;
        this.reportedAmount = 0;
    }

    public Player getPlayer() {
        if (this.player == null)
            this.player = Bukkit.getPlayer(this.getUUID());
        return this.player;
    }

    public void clearPlayer() {
        this.player = null;
    }

    public List<Report> getReports() {
        return reports;
    }

    public int getReportedAmount() {
        return reportedAmount;
    }

    public int getUsersReported() {
        return usersReported;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setReportedAmount(int reportedAmount) {
        this.reportedAmount = reportedAmount;
    }

    public void setUsersReported(int usersReported) {
        this.usersReported = usersReported;
    }
}
