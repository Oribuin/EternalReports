package xyz.oribuin.eternalreports.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.managers.DataManager
import xyz.oribuin.eternalreports.managers.MessageManager
import xyz.oribuin.eternalreports.managers.ReportManager
import xyz.oribuin.eternalreports.menus.ReportsMenu
import xyz.oribuin.eternalreports.utils.StringPlaceholders

class CmdReports(override val plugin: EternalReports) : OriCommand(plugin, "reports") {

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
        val messageManager = plugin.getManager(MessageManager::class)
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
                .addPlaceholder("reporter", report.sender.name)
                .addPlaceholder("reported", report.reported.name)
                .addPlaceholder("reason", report.reason)
                .addPlaceholder("id", report.id).build()

        if (report.isResolved) {
            messageManager.sendMessage(sender, "commands.unresolved-report", placeholders)
            plugin.getManager(DataManager::class).resolveReport(report, false)
            report.isResolved = false

            this.plugin.logger.info(sender.name + " has resolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")

        } else {
            messageManager.sendMessage(sender, "commands.resolved-report", placeholders)
            plugin.getManager(DataManager::class).resolveReport(report, true)
            report.isResolved = true

            this.plugin.logger.info(sender.name + " has unresolved ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")
        }
    }

    private fun onRemoveCommand(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)
        if (!sender.hasPermission("eternalreports.remove")) {
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
                .addPlaceholder("reporter", report.sender.name)
                .addPlaceholder("reported", report.reported.name)
                .addPlaceholder("reason", report.reason)
                .addPlaceholder("id", report.id).build()

        messageManager.sendMessage(sender, "commands.removed-report", placeholders)
        plugin.getManager(DataManager::class).deleteReport(report)

        this.plugin.logger.info(sender.name + " has removed ${report.sender.name}'s report on ${report.reported.name} for ${report.reason}")
    }

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)
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

            "delete" -> {
                this.onRemoveCommand(sender, args)
            }
            else -> {
                messageManager.sendMessage(sender, "unknown-command")
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val suggestions = mutableListOf<String>()
        if (args.isEmpty() || args.size == 1) {
            val subCommand = if (args.isEmpty()) "" else args[0]
            val commands = mutableListOf<String>()
            if (sender.hasPermission("eternalreports.reload"))
                commands.add("reload")

            if (sender.hasPermission("eternalreports.menu"))
                commands.add("menu")

            if (sender.hasPermission("eternalreports.resolve"))
                commands.add("resolve")

            if (sender.hasPermission("eternalreports.remove"))
                commands.add("remove")


            StringUtil.copyPartialMatches(subCommand, commands, suggestions)
            return null
        } else if (args.size == 2) {

            when (args[1].toLowerCase()) {
                "menu" -> {
                    val players = mutableListOf<String>()

                    Bukkit.getOnlinePlayers().stream().filter { player -> !player.hasMetadata("vanished") }.forEach { player -> players.add(player.name) }
                    StringUtil.copyPartialMatches(args[1].toLowerCase(), players, suggestions)
                }

                "resolve", "remove" -> {
                    val reportIds = mutableListOf<String>()

                    plugin.getManager(ReportManager::class).reports.forEach { report -> reportIds.add(report.id.toString()) }
                    StringUtil.copyPartialMatches(args[1].toLowerCase(), reportIds, suggestions)
                }
            }
        } else {
            return null
        }
        return suggestions
    }

}