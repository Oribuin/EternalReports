package xyz.oribuin.eternalreports.command

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.event.PlayerReportEvent
import xyz.oribuin.eternalreports.manager.ConfigManager
import xyz.oribuin.eternalreports.manager.DataManager
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.eternalreports.util.PluginUtils
import xyz.oribuin.orilibrary.command.OriCommand
import xyz.oribuin.orilibrary.util.StringPlaceholders
import java.util.*

class CmdReport(plugin: EternalReports) : OriCommand(plugin, "report") {

    private val cooldowns = mutableMapOf<UUID, Long>()

    override fun executeCommand(sender: CommandSender, args: Array<String>, label: String) {
        plugin as EternalReports

        val msg = plugin.getManager(MessageManager::class.java)
        val reportManager = plugin.getManager(ReportManager::class.java)
        val dataManager = plugin.getManager(DataManager::class.java)

        // Check if sender is player
        if (sender !is Player) {
            msg.sendMessage(sender, "player-only")
            return
        }

        // Check arguments
        if (args.size <= 1) {
            msg.sendMessage(sender, "invalid-arguments")
            return
        }


        // Reported user
        var reported: OfflinePlayer? = null
        for (pl in plugin.server.offlinePlayers) {
            pl ?: return
            if (pl.isBanned || !pl.hasPlayedBefore() || pl.name != args[0]) {
                msg.sendMessage(sender, "invalid-player")
                break
            }

            reported = pl
        }

        // Check if reported user is null
        if (reported == null || reported.player == null) {
            msg.sendMessage(sender, "invalid-player")
            return
        }

        val player = reported.player ?: return

        // Check if the player has permission to bypass report
        if (player.hasPermission("eternalreports.bypass")) {
            msg.sendMessage(sender, "has-bypass")
            return
        }

        // Report reason
        val reason = java.lang.String.join(" ", *args).substring(args[0].length + 1)
        val report = Report(reportManager.reports.size + 1, sender, reported, reason, false, System.currentTimeMillis())

        // Create Placeholders
        val placeholders = StringPlaceholders.builder()
            .addPlaceholder("sender", report.sender.name)
            .addPlaceholder("reported", report.reported.name)
            .addPlaceholder("reason", report.reason)
            .addPlaceholder("report_id", report.id)
            .addPlaceholder("time", PluginUtils.formatTime(report.time))
            .build()

        if (reportManager.reports.contains(report)) {
            msg.sendMessage(sender, "report-exists", placeholders)
            return
        }

        if (cooldowns.containsKey(sender.uniqueId)) {
            val secondsLeft = (cooldowns[sender.uniqueId]
                ?: return).div(1000).plus(ConfigManager.Setting.COOLDOWN.long).minus(System.currentTimeMillis().div(1000))

            if (secondsLeft > 0) {
                msg.sendMessage(sender, "cooldown", StringPlaceholders.single("cooldown", secondsLeft))
                return
            }
        }

        cooldowns[sender.uniqueId] = System.currentTimeMillis()

        val event = PlayerReportEvent(sender, report)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return
        }

        // Send the command sender the report message
        msg.sendMessage(sender, "commands.reported-user", placeholders)

        // Message staff members with alerts

        Bukkit.getOnlinePlayers().stream()
            .filter { it.hasPermission("eternalreports.alerts") && plugin.toggleList.contains(it.uniqueId) }
            .forEach { staffMember ->
                if (ConfigManager.Setting.ALERT_SETTINGS_SOUND_ENABLED.boolean) {

                    // Why such a long method kotlin?
                    ConfigManager.Setting.ALERT_SETTINGS_SOUND.string.let { Sound.valueOf(it) }.let { staffMember.playSound(staffMember.location, it, ConfigManager.Setting.ALERT_SETTINGS_SOUND_VOLUME.float, 1.toFloat()) }
                }
                msg.sendMessage(staffMember, "alerts.user-reported", placeholders)
            }

        dataManager.createReport(sender, reported, reason)
        dataManager.updateReportsMade(sender, dataManager.getReportsMade(sender) + 1)
        reported.player?.let { reported.player?.let { dataManager.getReportsMade(it).plus(1) }?.let { it1 -> dataManager.updateReportsAgainst(it, it1) } }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val suggestions = mutableListOf<String>()

        return if (args.size == 2) {
            StringUtil.copyPartialMatches(args[1].toLowerCase(), setOf("<reason>"), suggestions)
        } else {
            return suggestions
        }
    }

    override fun addSubCommands() {
        // Unused
    }
}