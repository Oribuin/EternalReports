package xyz.oribuin.eternalreports.managers

import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.ReportPlayer
import java.sql.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ReportManager(plugin: EternalReports) : Manager(plugin) {
    private val reportPlayers: MutableMap<UUID, ReportPlayer>

    init {
        reportPlayers = ConcurrentHashMap()
    }

    override fun reload() {
        reportPlayers.clear()
    }

    fun getReportPlayers(): Collection<ReportPlayer> {
        return reportPlayers.values
    }

    fun addReportedPlayer(player: ReportPlayer) {
        reportPlayers[player.uuid] = player
    }


    var globalReportCount = 0
        get() {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    field = result.getInt(1)
                }
            }

            return field
        }

    var resolvedReportCount = 0
        get() {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE resolved = true"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    field = result.getInt(1)
                }
            }

            return field
        }

    var unresolvedReportCount = 0
        get() {
            plugin.connector.connect { connection: Connection ->
                val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE resolved = true"
                connection.prepareStatement(query).use { statement ->
                    val result = statement.executeQuery()
                    result.next()
                    field = result.getInt(1)
                }
            }

            return field
        }
    
    fun getReportTotal(player: Player) : Int {
        var total = 0
        plugin.connector.connect { connection: Connection ->
            val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId}"
            connection.prepareStatement(query).use { statement ->
                val result = statement.executeQuery()
                result.next()
                total = result.getInt(1)
            }
        }

        return total
    }

    fun getResolvedReportTotal(player: Player) : Int {
        var total = 0
        plugin.connector.connect { connection: Connection ->
            val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId} AND resolved = true"
            connection.prepareStatement(query).use { statement ->
                val result = statement.executeQuery()
                result.next()
                total = result.getInt(1)
            }
        }

        return total
    }

    fun getUnresolvedReportTotal(player: Player) : Int {
        var total = 0
        plugin.connector.connect { connection: Connection ->
            val query = "SELECT COUNT (*) FROM ${plugin.dataManager.tablePrefix}reports WHERE sender = ${player.uniqueId} AND resolved = false"
            connection.prepareStatement(query).use { statement ->
                val result = statement.executeQuery()
                result.next()
                total = result.getInt(1)
            }
        }

        return total
    }

}