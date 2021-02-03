package xyz.oribuin.eternalreports.obj

import org.bukkit.OfflinePlayer

data class Report(val id: Int, val sender: OfflinePlayer, val reported: OfflinePlayer, var reason: String, var isResolved: Boolean = false, val time: Long)