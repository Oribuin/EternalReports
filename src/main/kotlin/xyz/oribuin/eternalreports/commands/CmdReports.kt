package xyz.oribuin.eternalreports.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.data.StaffMember
import xyz.oribuin.eternalreports.managers.ConfigManager
import xyz.oribuin.eternalreports.managers.DataManager
import xyz.oribuin.eternalreports.managers.MessageManager
import xyz.oribuin.eternalreports.managers.ReportManager
import xyz.oribuin.eternalreports.menus.ReportsMenu
import xyz.oribuin.eternalreports.utils.HexUtils
import xyz.oribuin.eternalreports.utils.PluginUtils
import xyz.oribuin.eternalreports.utils.StringPlaceholders
import java.util.*
import kotlin.collections.ArrayList

class CmdReports(override val plugin: EternalReports) : OriCommand(plugin, "reports") {

    private val messageManager = plugin.getManager(MessageManager::class)


    private fun onReloadCommand(sender: CommandSender) {
        val messageManager = plugin.getManager(MessageManager::class)

        if (!sender.hasPermission("eternalreports.reload")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        this.plugin.reload()
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))
    }

    private fun onResolveCommand(sender: CommandSender, args: Array<String>) {

        if (!sender.hasPermission("eternalreports.resolve")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        if (args.size == 1) {
            messageManager.sendMessage(sender, "invalid-arguments")
            return
        }

        val reports = plugin.getManager(ReportManager::class).reports.filter { report -> report.id == args[1].toInt() }

        if (reports.isEmpty()) {
            messageManager.sendMessage(sender, "invalid-report")
            return
        }

        val report = reports[0]

        val placeholders = StringPlaceholders.builder()
                .addPlaceholder("sender", sender.name)
                .addPlaceholder("reported", report.reported.name)
                .addPlaceholder("reason", report.reason)
                .addPlaceholder("report_id", report.id)
                .addPlaceholder("resolved", resolvedFormatted(report.isResolved)).build()

        if (report.isResolved) {
            messageManager.sendMessage(sender, "commands.unresolved-report", placeholders)
            PluginUtils.debug("Unresolving report ${report.id}.")
            plugin.getManager(DataManager::class).resolveReport(report, false)
            report.isResolved = false

            this.plugin.logger.info(sender.name + " has resolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")

        } else {
            messageManager.sendMessage(sender, "commands.resolved-report", placeholders)
            plugin.getManager(DataManager::class).resolveReport(report, true)
            report.isResolved = true

            this.plugin.logger.info(sender.name + " has unresolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")
        }

        // Message staff members with alerts
        Bukkit.getOnlinePlayers().stream()
                .filter { staffMember: Player -> staffMember.hasPermission("eternalreports.alerts") && StaffMember(staffMember).hasNotifications() }
                .forEach { staffMember: Player ->
                    if (ConfigManager.Setting.ALERT_SETTINGS_SOUND_ENABLED.boolean) {

                        // Why such a long method kotlin?
                        ConfigManager.Setting.ALERT_SETTINGS_SOUND.string.let { Sound.valueOf(it) }.let { staffMember.playSound(staffMember.location, it, ConfigManager.Setting.ALERT_SETTINGS_SOUND_VOLUME.float, 1.toFloat()) }
                    }
                    messageManager.sendMessage(staffMember, "alerts.report-resolved", placeholders)
                }
    }

    private fun onRemoveCommand(sender: CommandSender, args: Array<String>) {
        if (!sender.hasPermission("eternalreports.delete")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        if (args.size == 1) {
            messageManager.sendMessage(sender, "invalid-arguments")
            return
        }

        val reports = plugin.getManager(ReportManager::class).reports.filter { report -> report.id == args[1].toInt() }

        if (reports.isEmpty()) {
            messageManager.sendMessage(sender, "invalid-report")
            return
        }

        val report = reports[0]

        val placeholders = StringPlaceholders.builder()
                .addPlaceholder("sender", sender.name)
                .addPlaceholder("reported", report.reported.name)
                .addPlaceholder("reason", report.reason)
                .addPlaceholder("report_id", report.id).build()

        messageManager.sendMessage(sender, "commands.removed-report", placeholders)
        plugin.getManager(DataManager::class).deleteReport(report)

        this.plugin.logger.info(sender.name + " has removed ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")
    }

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty() || args.size == 1 && args[0].toLowerCase() == "menu") {
            if (sender !is Player) {
                messageManager.sendMessage(sender, "player-only")
                return
            }

            if (!sender.hasPermission("eternalreports.menu")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            ReportsMenu(plugin, sender).openMenu()
            return
        }

        if (args.size == 2 && args[0].toLowerCase() == "menu") {
            val mentioned = Bukkit.getPlayer(args[1])

            if (!sender.hasPermission("eternalreports.menu.other")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                messageManager.sendMessage(sender, "invalid-player")
                return
            }

            ReportsMenu(plugin, mentioned).openMenu()
        }

        when (args[0].toLowerCase()) {
            "reload" -> {
                this.onReloadCommand(sender)
            }

            "resolve" -> {
                this.onResolveCommand(sender, args)
            }

            "delete", "remove" -> {
                this.onRemoveCommand(sender, args)
            }
            else -> {
                messageManager.sendMessage(sender, "unknown-command")
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val deleteCmdList = listOf("delete", "remove")


        val suggestions: MutableList<String> = ArrayList()
        if (args.isEmpty() || args.size == 1) {
            val subCommand = if (args.isEmpty()) "" else args[0]

            val commands: MutableList<String> = ArrayList()

            if (sender.hasPermission("eternalreports.reload"))
                commands.add("reload")

            if (sender.hasPermission("eternalreports.menu"))
                commands.add("menu")

            if (sender.hasPermission("eternalreports.resolve"))
                commands.add("resolve")

            if (sender.hasPermission("eternalreports.delete"))
                commands.add("delete")

            StringUtil.copyPartialMatches(subCommand, commands, suggestions)
            return null
        } else if (args.size == 2) {
            if (args[0].toLowerCase() == "menu" && sender.hasPermission("eternalreports.menu.other")) {
                val players: MutableList<String> = ArrayList()
                Bukkit.getOnlinePlayers().stream().filter { player -> !player.hasPermission("vanished") }.forEach { player -> players.add(player.name) }

                StringUtil.copyPartialMatches(args[1].toLowerCase(), players, suggestions)

            } else if (args[0].toLowerCase() == "resolve" && sender.hasPermission("eternalreports.resolve") || deleteCmdList.contains(args[0].toLowerCase()) && sender.hasPermission("eternalreports.delete")) {

                val ids: MutableList<String> = ArrayList()
                plugin.getManager(ReportManager::class).reports.stream().forEach { t -> ids.add(t.id.toString()) }

                StringUtil.copyPartialMatches(args[1].toLowerCase(), ids, suggestions)
            }
        } else {
            return null
        }
        return suggestions
    }


    private fun resolvedFormatted(resolved: Boolean): String? {
        return if (resolved) {
            messageManager.messageConfig.getString("resolved-formatting.is-resolved")?.let { HexUtils.colorify(it) }
        } else {
            messageManager.messageConfig.getString("resolved-formatting.isNT-resolved")?.let { HexUtils.colorify(it) }
        }
    }

}