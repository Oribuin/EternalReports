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
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import java.util.*

class CmdReport(override val plugin: EternalReports) : OriCommand(plugin, "report") {

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        val msg = plugin.messageManager

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

        // Create Placeholders
        val placeholders = StringPlaceholders.builder()
                .addPlaceholder("sender", sender.getName())
                .addPlaceholder("player", reported.name)
                .addPlaceholder("reason", reason)
                .build()

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

        plugin.dataManager.createReport(sender, reported, reason)

        val event = PlayerReportEvent(Report(this.plugin.reportManager.globalReportCount, sender, reported, reason, false))
        Bukkit.getPluginManager().callEvent(event)
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