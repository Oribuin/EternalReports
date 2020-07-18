package xyz.oribuin.eternalreports.data

import org.bukkit.OfflinePlayer

data class Report(val sender: OfflinePlayer, val reported: OfflinePlayer, var reason: String, val resolved: Boolean) {
    var isResolved = false
}