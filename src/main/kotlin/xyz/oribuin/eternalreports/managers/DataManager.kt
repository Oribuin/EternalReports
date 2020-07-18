package xyz.oribuin.eternalreports.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
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
                "CREATE TABLE IF NOT EXISTS " + tablePrefix + "reports (sender TXT, reported TXT, reason TXT, resolved BOOLEAN, PRIMARY KEY(sender, reported, reason))",
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

    fun createReport(sender: Player, reported: Player, reason: String) {

        async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val createReport = "INSERT INTO ${this.tablePrefix}reports (sender, reported, reason, resolved) VALUES (?, ?, ?, ?)"
                connection.prepareStatement(createReport).use { statement ->
                    statement.setString(1, sender.uniqueId.toString())
                    statement.setString(2, reported.uniqueId.toString())
                    statement.setString(3, reason)
                    statement.setBoolean(4, false)
                    statement.executeUpdate()
                }

            }
        })
    }

    fun getReportedPlayer(uuid: UUID): ReportPlayer? {
        for (pl in plugin.reportManager.getReportPlayers()) if (pl.uuid == uuid) return pl
        return null
    }

    fun getReportPlayer(uuid: UUID, callback: Consumer<ReportPlayer>) {
        val cache = getReportedPlayer(uuid)
        if (cache != null) {
            callback.accept(cache)
            return
        }
        async(Runnable { plugin.connector.connect { } })
    }

    /**
     * Asynchronizes the callback with it's own thread unless it is already not on the main thread
     *
     * @param asyncCallback The callback to run on a separate thread
     */
    private fun async(asyncCallback: Runnable) {
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