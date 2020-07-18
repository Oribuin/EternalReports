package xyz.oribuin.eternalreports.data

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ReportPlayer(val uuid: UUID) {
    private var player: Player? = null
    private val reports: MutableList<Report>
    var usersReported: Int
    var reportedAmount: Int

    fun getPlayer(): Player? {
        if (player == null)
            player = Bukkit.getPlayer(uuid)
        return player
    }

    fun clearPlayer() {
        player = null
    }

    init {
        reports = ArrayList()
        usersReported = 0
        reportedAmount = 0
    }
}