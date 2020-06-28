package xyz.oribuin.eternalreports.managers;

import org.bukkit.Bukkit;
import xyz.oribuin.eternalreports.EternalReports;
import xyz.oribuin.eternalreports.data.ReportPlayer;
import xyz.oribuin.eternalreports.database.DatabaseConnector;
import xyz.oribuin.eternalreports.database.SQLiteConnector;

import java.util.UUID;
import java.util.function.Consumer;

public class DataManager extends Manager {

    private DatabaseConnector connector;

    public DataManager(EternalReports plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        if (this.connector != null) {
            this.connector.closeConnection();
        }

        this.connector = new SQLiteConnector(this.plugin);
    }

    public ReportPlayer getReportedPlayer(UUID uuid) {
        for (ReportPlayer pl : this.plugin.getReportManager().getReportPlayers())
            if (pl.getUUID().equals(uuid))
                return pl;

        return null;
    }

    public void getReportPlayer(UUID uuid, Consumer<ReportPlayer> callback) {

        ReportPlayer cache = this.getReportedPlayer(uuid);
        if (cache != null) {
            callback.accept(cache);
            return;
        }

        this.async(() -> this.plugin.getConnector().connect(connection -> {
            // TODO: Create Report Data
        }));
    }
    /**
     * Asynchronizes the callback with it's own thread unless it is already not on the main thread
     *
     * @param asyncCallback The callback to run on a separate thread
     */
    private void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, asyncCallback);
    }

    /**
     * Synchronizes the callback with the main thread
     *
     * @param syncCallback The callback to run on the main thread
     */
    private void sync(Runnable syncCallback) {
        Bukkit.getScheduler().runTask(this.plugin, syncCallback);
    }

    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }
}
