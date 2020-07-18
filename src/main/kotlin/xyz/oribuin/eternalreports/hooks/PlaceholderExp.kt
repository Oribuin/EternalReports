package xyz.oribuin.eternalreports.hooks

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports

class PlaceholderExp(private val plugin: EternalReports) : PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player, placeholders: String): String? {

        if (placeholders.toLowerCase() == "total")
            return plugin.reportManager.globalReportCount.toString()

        if (placeholders.toLowerCase() == "resolved")
            return plugin.reportManager.resolvedReportCount.toString()

        if (placeholders.toLowerCase() == "unresolved")
            return plugin.reportManager.unresolvedReportCount.toString()

        if (placeholders.toLowerCase() == "player_total")
            return plugin.reportManager.getReportTotal(player).toString()

        if (placeholders.toLowerCase() == "player_resolved")
            return plugin.reportManager.getResolvedReportTotal(player).toString()

        if (placeholders.toLowerCase() == "player_unresolved")
            return plugin.reportManager.getUnresolvedReportTotal(player).toString()

        return null
    }

    override fun persist(): Boolean {
        return true
    }

    override fun getIdentifier(): String {
        return plugin.description.name.toLowerCase()
    }

    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    override fun getVersion(): String {
        return plugin.description.version
    }
}