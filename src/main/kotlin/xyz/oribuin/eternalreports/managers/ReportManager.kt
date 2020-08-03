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
        this.reports.clear()
        this.registerReports()
    }

    private fun registerReports() {
        this.plugin.dataManager.async(Runnable {
            plugin.dataManager.connector?.connect { connection: Connection ->
                val query = "SELECT * FROM ${tablePrefix}reports"

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

    private fun removeReports() {
        for (report in reports) {
            this.plugin.dataManager.async(Runnable {
                this.plugin.dataManager.connector?.connect { connection ->
                    val query = "REPLACE INTO ${tablePrefix}reports (id, sender, reported, reason, resolved) VALUES (?, ?, ?, ?, ?)"

                    connection.prepareStatement(query).use { statement ->
                        statement.setInt(1, report.id)
                        statement.setString(2, report.sender.uniqueId.toString())
                        statement.setString(3, report.reported.uniqueId.toString())
                        statement.setString(4, report.reason)
                        statement.setBoolean(5, report.isResolved)

                        statement.executeUpdate()
                        reports.remove(report)
                    }
                }
            })
        }
    }

    fun getReportTotal(player: Player): Int {
        return reports.stream().filter { x -> x.sender.uniqueId == player.uniqueId }.count().toInt()
    }

    fun getResolvedReportTotal(player: Player): Int {
        return reports.stream().filter { x -> x.sender.uniqueId == player.uniqueId && x.isResolved }.count().toInt()
    }

    fun getUnresolvedReportTotal(player: Player): Int {
        return reports.stream().filter { x -> x.sender.uniqueId == player.uniqueId && !x.isResolved }.count().toInt()
    }

    override fun disable() {
        this.removeReports()
    }

    private val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'
}