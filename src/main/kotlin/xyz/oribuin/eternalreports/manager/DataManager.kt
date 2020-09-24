package xyz.oribuin.eternalreports.manager

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.database.DatabaseConnector
import xyz.oribuin.eternalreports.database.MySQLConnector
import xyz.oribuin.eternalreports.database.SQLiteConnector
import xyz.oribuin.eternalreports.util.FileUtils.createFile
import java.sql.Connection

class DataManager(plugin: EternalReports) : Manager(plugin) {
    var connector: DatabaseConnector? = null
    private var reportsMade: Int = 0
    private var reportsAgainst: Int = 0

    override fun reload() {

        try {
            if (ConfigManager.Setting.SQL_ENABLED.boolean) {
                val hostname = ConfigManager.Setting.SQL_HOSTNAME.string
                val port = ConfigManager.Setting.SQL_PORT.int
                val database = ConfigManager.Setting.SQL_DATABASENAME.string
                val username = ConfigManager.Setting.SQL_USERNAME.string
                val password = ConfigManager.Setting.SQL_PASSWORD.string
                val useSSL = ConfigManager.Setting.SQL_USE_SSL.boolean

                this.connector = MySQLConnector(this.plugin, hostname, port, database, username, password, useSSL)
                this.plugin.logger.info("Now using MySQL for the plugin Database.")
            } else {
                createFile(plugin, "eternalreports.db")

                this.connector = SQLiteConnector(this.plugin)
                this.plugin.logger.info("Now using SQLite for the Plugin Database.")
            }

            this.createTables()

        } catch (ex: Exception) {
            this.plugin.logger.severe("Fatal error connecting to Database, Plugin has disabled itself.")
            Bukkit.getPluginManager().disablePlugin(this.plugin)
            ex.printStackTrace()
        }

    }

    private fun createTables() {
        val queries = arrayOf(
                "CREATE TABLE IF NOT EXISTS ${tablePrefix}reports (id INT, sender TXT, reported TXT, reason TXT, resolved BOOLEAN, time LONG, PRIMARY KEY(sender, reported, reason, time))",
                "CREATE TABLE IF NOT EXISTS ${tablePrefix}users (user TXT, reports INT, reported INT, PRIMARY KEY(user))"
        )
        async {
            connector?.connect { connection: Connection ->
                for (string in queries) {
                    connection.prepareStatement(string).use { statement -> statement.executeUpdate() }
                }
            }
        }
    }

    fun createReport(sender: Player, reported: OfflinePlayer, reason: String) {

        val reportManager = plugin.getManager(ReportManager::class)
        val reportCount = mutableListOf<Int>()
        reportManager.reports.forEach { report -> reportCount.add(report.id) }


        async {
            connector?.connect { connection: Connection ->
                val createReport = "REPLACE INTO ${this.tablePrefix}reports (id, sender, reported, reason, resolved, time) VALUES (?, ?, ?, ?, ?, ?)"
                connection.prepareStatement(createReport).use { statement ->
                    statement.setInt(1, reportManager.getNextReportId(reportCount))
                    statement.setString(2, sender.uniqueId.toString())
                    statement.setString(3, reported.uniqueId.toString())
                    statement.setString(4, reason)
                    statement.setBoolean(5, false)
                    statement.setLong(6, System.currentTimeMillis())
                    statement.executeUpdate()
                }

                reportManager.reports.add(Report(reportManager.getNextReportId(reportCount), sender, reported, reason, false, System.currentTimeMillis()))

            }
        }
    }

    fun deleteReport(report: Report) {
        val reportManager = plugin.getManager(ReportManager::class)

        async {
            connector?.connect { connection: Connection ->
                val removeReport = "DELETE FROM ${tablePrefix}reports WHERE id = ? AND sender = ? AND reported = ? AND reason = ?"
                connection.prepareStatement(removeReport).use { statement ->
                    statement.setInt(1, report.id)
                    statement.setString(2, report.sender.uniqueId.toString())
                    statement.setString(3, report.reported.uniqueId.toString())
                    statement.setString(4, report.reason)
                    statement.executeUpdate()
                }

            }

            reportManager.reports.remove(report)
        }
    }

    fun resolveReport(report: Report, resolved: Boolean) {
        val reportManager = plugin.getManager(ReportManager::class)

        async {
            connector?.connect { connection: Connection ->
                val removeReport = "REPLACE INTO ${tablePrefix}reports (id, sender, reported, reason, resolved, time) VALUES (?, ?, ?, ?, ?, ?)"
                connection.prepareStatement(removeReport).use { statement ->
                    statement.setInt(1, report.id)
                    statement.setString(2, report.sender.uniqueId.toString())
                    statement.setString(3, report.reported.uniqueId.toString())
                    statement.setString(4, report.reason)
                    statement.setBoolean(5, resolved)
                    statement.setLong(6, report.time)
                    statement.executeUpdate()
                }

                if (reportManager.reports.contains(report)) {
                    reportManager.reports.remove(report)
                    reportManager.reports.add(Report(report.id, report.sender, report.reported, report.reason, resolved, report.time))
                }
            }
        }
    }

    fun updateReportsMade(player: Player, count: Int) {
        async {
            connector?.connect { connection ->
                val updateUser = "REPLACE INTO ${tablePrefix}users (user, reports) VALUES (?, ?)"
                connection.prepareStatement(updateUser).use { statement ->
                    statement.setString(1, player.uniqueId.toString())
                    statement.setInt(2, count)
                    statement.executeUpdate()
                }
            }
        }
    }

    fun updateReportsAgainst(player: Player, count: Int) {
        async {
            connector?.connect { connection ->
                val updateUser = "REPLACE INTO ${tablePrefix}users (user, reported) VALUES (?, ?)"
                connection.prepareStatement(updateUser).use { statement ->
                    statement.setString(1, player.uniqueId.toString())
                    statement.setInt(2, count)
                    statement.executeUpdate()
                }
            }
        }
    }

    fun getReportsMade(player: Player): Int {
        connector?.connect { connection ->
            val query = "SELECT reports FROM ${tablePrefix}users WHERE user = ?"
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, player.uniqueId.toString())
                val result = statement.executeQuery()
                if (result.next())
                    reportsMade = result.getInt(1)
            }
        }

        return reportsMade
    }

    fun getReportsAgainst(player: Player): Int {
        connector?.connect { connection ->
            val query = "SELECT reported FROM ${tablePrefix}users WHERE user = ?"
            connection.prepareStatement(query).use { statement ->
                statement.setString(1, player.uniqueId.toString())
                val result = statement.executeQuery()
                if (result.next())
                    reportsAgainst = result.getInt(1)
            }
        }

        return reportsAgainst
    }

    /**
     * Asynchronizes the callback with it's own thread unless it is already not on the main thread
     *
     * @param asyncCallback The callback to run on a separate thread
     */
    private fun async(asyncCallback: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncCallback)
    }

    private val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'

    override fun disable() {
        // Unused
    }
}