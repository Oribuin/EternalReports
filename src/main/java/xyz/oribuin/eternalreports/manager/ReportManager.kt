package xyz.oribuin.eternalreports.manager

import org.bukkit.Bukkit
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.obj.Report
import xyz.oribuin.orilibrary.manager.Manager
import java.util.*

class ReportManager(plugin: EternalReports) : Manager(plugin) {
    val reports = mutableListOf<Report>()
    val resolvedReports = reports.stream().filter { x -> x.isResolved }.count().toInt()
    val unresolvedReports = reports.stream().filter { x -> !x.isResolved }.count().toInt()

    override fun enable() {
        this.reports.clear()
        this.registerReports()
    }

    private fun registerReports() {
        val data = plugin.getManager(DataManager::class.java)

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

                        val count = reports.stream().filter { t -> t == report }.count()

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

    /**
     * @author Esophose
     *
     * Gets the smallest positive integer greater than 0 from a list
     *
     * @param existingIds The list containing non-available ids
     * @return The smallest positive integer not in the given list
     */
    fun getNextReportId(existingIds: Collection<Int>): Int {
        val copy = existingIds.sorted().toMutableList()
        copy.removeIf { it <= 0 }

        var current = 1
        for (i in copy) {
            if (i == current) {
                current++
            } else break
        }

        return current
    }

    override fun disable() {
        this.reports.clear()
    }

    private val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'
}