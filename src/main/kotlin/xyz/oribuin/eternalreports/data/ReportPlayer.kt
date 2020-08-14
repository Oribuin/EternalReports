package xyz.oribuin.eternalreports.data

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

data class ReportPlayer(val uuid: UUID) {
    private var player: OfflinePlayer? = null
    private val reports: MutableList<Report>
    var usersReported: Int
    var reportedAmount: Int

    fun getPlayer(): OfflinePlayer? {
        if (player == null)
            player = Bukkit.getOfflinePlayer(uuid)
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