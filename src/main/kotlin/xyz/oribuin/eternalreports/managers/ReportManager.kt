package xyz.oribuin.eternalreports.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.utils.PluginUtils
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
        val data = plugin.getManager(DataManager::class)

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            data.async(Runnable {
                data.connector?.connect { connection ->
                    val query = "SELECT * FROM ${tablePrefix}reports"

                    connection.prepareStatement(query).use { statement ->
                        val result = statement.executeQuery()
                        while (result.next()) {

                            val report = Report(result.getInt("id"), Bukkit.getOfflinePlayer(UUID.fromString(result.getString("sender"))), Bukkit.getOfflinePlayer(UUID.fromString(result.getString("reported"))), result.getString("reason"), result.getBoolean("resolved"))


                            println("[DB Reports] ID ${report.id} sender ${report.sender} reported ${report.reported} reason ${report.reason} resolved ${report.isResolved}")

                            if (!reports.contains(report)) {
                                //PluginUtils.debug("Registering report ${report.id} into val reports = mutableListOf<Report>()")
                                reports.add(report)
                            }
                        }

                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            for (rep in reports) {
                                println("[List Reports] ID ${rep.id} sender ${rep.sender} reported ${rep.reported} reason ${rep.reason} resolved ${rep.isResolved}")
                            }
                        }, 30)
                    }
                }
            })

        }, 10)
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
        this.reports.clear()
    }

    private val tablePrefix: String
        get() = plugin.description.name.toLowerCase() + '_'
}