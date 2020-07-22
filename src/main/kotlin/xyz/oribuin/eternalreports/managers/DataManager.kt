package xyz.oribuin.eternalreports.managers

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.data.ReportPlayer
import xyz.oribuin.eternalreports.utils.FileUtils.createFile
import java.sql.Connection
import java.util.*
import java.util.function.Consumer

class DataManager(plugin: EternalReports) : Manager(plugin) {

    override fun reload() {
        createFile(plugin, "eternalreports.db")
        createTables()
    }

    private fun createTables() {
        val queries = arrayOf(
                "CREATE TABLE IF NOT EXISTS " + tablePrefix + "reports (id INT, sender TXT, reported TXT, reason TXT, resolved BOOLEAN, PRIMARY KEY(sender, reported, reason))",
                "CREATE TABLE IF NOT EXISTS " + tablePrefix + "users (user TXT, reports, reported, PRIMARY KEY(user))"
        )
        async(Runnable {
            plugin.connector.connect { connection: Connection ->
                for (string in queries) {
                    connection.prepareStatement(string).use { statement -> statement.executeUpdate() }
                }
            }
        })
    }

    fun createReport(sender: Player, reported: OfflinePlayer, reason: String) {

        async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val createReport = "REPLACE INTO ${this.tablePrefix}reports (id, sender, reported, reason, resolved) VALUES (?, ?, ?, ?, ?)"
                connection.prepareStatement(createReport).use { statement ->
                    statement.setInt(1, this.plugin.reportManager.globalReportCount)
                    statement.setString(2, sender.uniqueId.toString())
                    statement.setString(3, reported.uniqueId.toString())
                    statement.setString(4, reason)
                    statement.setBoolean(5, false)
                    statement.executeUpdate()
                }

            }
        })
    }

    fun deleteReport(report: Report) {
        async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val removeReport = "DELETE FROM ${tablePrefix}reports WHERE id = ? AND sender = ? AND reported = ? AND reason = ?"
                connection.prepareStatement(removeReport).use { statement ->
                    statement.setInt(1, report.id)
                    statement.setString(2, report.sender.uniqueId.toString())
                    statement.setString(3, report.reported.uniqueId.toString())
                    statement.setString(4, report.reason)
                }
            }
        })
    }

    fun resolveReport(report: Report, resolved: Boolean) {
        async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val removeReport = "REPLACE INTO ${tablePrefix}reports (id, sender, reported, reason, resolved) VALUES (?, ?, ?, ?, ?)"
                connection.prepareStatement(removeReport).use { statement ->
                    statement.setInt(1, report.id)
                    statement.setString(2, report.sender.uniqueId.toString())
                    statement.setString(3, report.reported.uniqueId.toString())
                    statement.setString(4, report.reason)
                    statement.setBoolean(5, resolved)
                }
            }
        })
    }

    /**
     * Asynchronizes the callback with it's own thread unless it is already not on the main thread
     *
     * @param asyncCallback The callback to run on a separate thread
     */
    fun async(asyncCallback: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncCallback)
    }

    /**
     * Synchronizes the callback with the main thread
     *
     * @param syncCallback The callback to run on the main thread
     */
    private fun sync(syncCallback: Runnable) {
        Bukkit.getScheduler().runTask(plugin, syncCallback)
    }

    val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'
}