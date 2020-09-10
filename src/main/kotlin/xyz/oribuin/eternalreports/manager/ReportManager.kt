package xyz.oribuin.eternalreports.manager

import org.bukkit.Bukkit
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import java.util.*

class ReportManager(plugin: EternalReports) : Manager(plugin) {
    val reports = mutableListOf<Report>()
    val resolvedReports = reports.stream().filter { x -> x.isResolved }.count().toInt()
    val unresolvedReports = reports.stream().filter { x -> !x.isResolved }.count().toInt()
    override fun reload() {
        this.reports.clear()
        this.registerReports()
    }

    private fun registerReports() {
        val data = plugin.getManager(DataManager::class)


        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            data.connector?.connect { connection ->
                val query = "SELECT * FROM ${tablePrefix}reports"

                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    while (result.next()) {

                        val report = Report(result.getInt("id"), Bukkit.getOfflinePlayer(UUID.fromString(result.getString("sender"))), Bukkit.getOfflinePlayer(UUID.fromString(result.getString("reported"))), result.getString("reason"), result.getBoolean("resolved"), result.getLong("time"))

                        if (!reports.contains(report)) {
                            reports.add(report)
                        }

                        val count = reports.stream().filter { t -> t == report}.count()

                        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
                            while (count > 1) {
                                reports.stream().filter { t -> t == report }.forEach { t -> reports.remove(t) }
                            }
                        }, 0, 3)
                    }
                }
            }

        }, 10)
    }

    override fun disable() {
        this.reports.clear()
    }

    private val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'
}