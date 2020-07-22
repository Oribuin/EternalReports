package xyz.oribuin.eternalreports.managers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import java.sql.Connection
import java.util.*

class ReportManager(plugin: EternalReports) : Manager(plugin) {
    val reports = mutableListOf<Report>()

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

    var globalReportCount = 0
        get() {
            this.plugin.dataManager.async(Runnable {
                plugin.connector.connect { connection: Connection ->
                    val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports"
                    connection.prepareStatement(query).use { statement ->
                        val result = statement.executeQuery()
                        result.next()
                        field = result.getInt(1)
                    }
                }
            })

            return field
        }

    var resolvedReportCount = 0
        get() {
            this.plugin.dataManager.async(Runnable {
                plugin.connector.connect { connection: Connection ->
                    val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE resolved = true"
                    connection.prepareStatement(query).use { statement ->
                        val result = statement.executeQuery()
                        result.next()
                        field = result.getInt(1)
                    }
                }
            })

            return field
        }

    var unresolvedReportCount = 0
        get() {
            this.plugin.dataManager.async(Runnable {
                plugin.connector.connect { connection: Connection ->
                    val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE resolved = true"
                    connection.prepareStatement(query).use { statement ->
                        val result = statement.executeQuery()
                        result.next()
                        field = result.getInt(1)
                    }
                }

            })
            return field
        }

    fun getReportTotal(player: Player): Int {
        var total = 0
        this.plugin.dataManager.async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId}"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    total = result.getInt(1)
                }
            }
        })

        return total
    }

    fun getResolvedReportTotal(player: Player): Int {
        var total = 0
        this.plugin.dataManager.async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId} AND resolved = true"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    total = result.getInt(1)
                }
            }
        })
        return total
    }

    fun getUnresolvedReportTotal(player: Player): Int {
        var total = 0
        this.plugin.dataManager.async(Runnable {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId} AND resolved = false"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    total = result.getInt(1)
                }
            }
        })
        return total
    }
}