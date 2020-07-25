package xyz.oribuin.eternalreports.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import java.sql.Connection
import java.util.*

class ReportManager(plugin: EternalReports) : Manager(plugin) {
    val reports = mutableListOf<Report>()
    val globalReportCount = reports.size
    val resolvedReports = reports.stream().filter { x -> x.isResolved }.count().toInt()
    val unresolvedReports = reports.stream().filter { x -> !x.isResolved }.count().toInt()

    override fun reload() {
        reports.clear()
        this.registerReports()
    }

    private fun registerReports() {
        this.plugin.dataManager.async(Runnable {
            this.plugin.connector.connect { connection: Connection ->
                val query = "SELECT * FROM ${plugin.dataManager.tablePrefix}reports"

                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    while (result.next()) {

                        reports.add(Report(
                                result.getInt("id"), // ID
                                Bukkit.getOfflinePlayer(UUID.fromString(result.getString("sender"))), // Sender
                                Bukkit.getOfflinePlayer(UUID.fromString(result.getString("reported"))), // Reported
                                result.getString("reason"), // Reason
                                result.getBoolean("resolved"))) // Is resolved
                    }
                }
            }
        })
    }

    fun getReportTotal(player: Player): Int {
        return reports.stream().filter { t -> t.sender.uniqueId == player.uniqueId}.count().toInt()
    }

    fun getResolvedReportTotal(player: Player): Int {
        return reports.stream().filter{ t -> t.sender.uniqueId == player.uniqueId && t.isResolved}.count().toInt()
    }

    fun getUnresolvedReportTotal(player: Player): Int {
        return reports.stream().filter{ t -> t.sender.uniqueId == player.uniqueId && !t.isResolved}.count().toInt()
    }
}