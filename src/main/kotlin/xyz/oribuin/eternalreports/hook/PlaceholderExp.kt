package xyz.oribuin.eternalreports.hook

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.manager.DataManager
import xyz.oribuin.eternalreports.manager.ReportManager

class PlaceholderExp(private val plugin: EternalReports) : PlaceholderExpansion() {

    override fun onPlaceholderRequest(player: Player, placeholders: String): String? {

        if (placeholders.toLowerCase() == "total")
            return plugin.getManager(ReportManager::class).reports.size.toString()

        if (placeholders.toLowerCase() == "resolved")
            return plugin.getManager(ReportManager::class).resolvedReports.toString()

        if (placeholders.toLowerCase() == "unresolved")
            return plugin.getManager(ReportManager::class).unresolvedReports.toString()

        if (placeholders.toLowerCase() == "reports_made")
            return plugin.getManager(DataManager::class).getReportsMade(player).toString()

        if (placeholders.toLowerCase() == "reports_against")
            return plugin.getManager(DataManager::class).getReportsAgainst(player).toString()

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