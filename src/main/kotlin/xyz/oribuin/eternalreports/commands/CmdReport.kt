package xyz.oribuin.eternalreports.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.Report
import xyz.oribuin.eternalreports.data.StaffMember
import xyz.oribuin.eternalreports.events.PlayerReportEvent
import xyz.oribuin.eternalreports.managers.ConfigManager
import xyz.oribuin.eternalreports.managers.DataManager
import xyz.oribuin.eternalreports.managers.MessageManager
import xyz.oribuin.eternalreports.managers.ReportManager
import xyz.oribuin.eternalreports.utils.PluginUtils
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import java.util.*

class CmdReport(override val plugin: EternalReports) : OriCommand(plugin, "report") {

    private val cooldowns: MutableMap<UUID, Long> = HashMap()

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        val msg = plugin.getManager(MessageManager::class)
        val reportManager = plugin.getManager(ReportManager::class)

        // Check if sender is player
        if (sender !is Player) {
            msg.sendMessage(sender, "player-only")
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

        // Check arguments
        if (args.size <= 1) {
            msg.sendMessage(sender, "invalid-arguments")
            return
        }


        // Reported user
        val reported = Bukkit.getPlayer(args[0])?.uniqueId?.let { Bukkit.getOfflinePlayer(it) }

        // Check if reported user is null
        if (reported == null) {
            msg.sendMessage(sender, "invalid-player")
            return
        }

        /*
        // Check if the player has permission to bypass report
        if (reported.hasPermission("eternalreports.bypass")) {
            msg.sendMessage(sender, "has-bypass")
            return
        }
        /
         */

        // Report reason
        val reason = java.lang.String.join(" ", *args).substring(args[0].length + 1)
        val report = Report(plugin.getManager(ReportManager::class).reports.size + 1, sender, reported, reason, false, System.currentTimeMillis())

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

        // Send the command sender the report message
        msg.sendMessage(sender, "commands.reported-user", placeholders)

        // Message staff members with alerts
        Bukkit.getOnlinePlayers().stream()
                .filter { staffMember: Player -> staffMember.hasPermission("eternalreports.alerts") && StaffMember(staffMember).hasNotifications() }
                .forEach { staffMember: Player ->
                    if (ConfigManager.Setting.ALERT_SETTINGS_SOUND_ENABLED.boolean) {

                        // Why such a long method kotlin?
                        ConfigManager.Setting.ALERT_SETTINGS_SOUND.string.let { Sound.valueOf(it) }.let { staffMember.playSound(staffMember.location, it, ConfigManager.Setting.ALERT_SETTINGS_SOUND_VOLUME.float, 1.toFloat()) }
                    }
                    msg.sendMessage(staffMember, "alerts.user-reported", placeholders)
                }

        plugin.getManager(DataManager::class).createReport(sender, reported, reason)
        Bukkit.getPluginManager().callEvent(PlayerReportEvent(report))
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val suggestions: MutableList<String> = ArrayList()
        if (args.isEmpty() || args.size == 1) {
            val subCommand = if (args.isEmpty()) "" else args[0]

            val players: MutableList<String> = ArrayList()

            Bukkit.getOnlinePlayers().stream().filter { player: Player? -> !player!!.hasMetadata("vanished") }.forEachOrdered { player: Player? -> players.add(player!!.name) }

            StringUtil.copyPartialMatches(subCommand, players, suggestions)
            return null
        } else if (args.size == 2) {
            StringUtil.copyPartialMatches(args[1].toLowerCase(), setOf("<reason>"), suggestions)
        } else {
            return null
        }
        return suggestions
    }

}