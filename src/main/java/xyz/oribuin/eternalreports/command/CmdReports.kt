package xyz.oribuin.eternalreports.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.command.subcommand.*
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.manager.ReportManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.SubCommand

class CmdReports(val plugin: EternalReports) : OriCommand(plugin, "reports") {
    private val subcommands = mutableListOf<SubCommand>()

    private val messageManager = plugin.getManager(MessageManager::class.java)

    override fun executeCommand(sender: CommandSender, args: Array<String>) {

        for (cmd in subcommands) {
            if (args.isEmpty()) {
                messageManager.sendMessage(sender, "unknown-command")
                break
            }

            if (args.isNotEmpty() && cmd.names.contains(args[0].toLowerCase())) {
                cmd.executeArgument(sender, args)
                break
            }
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): MutableList<String>? {

        val deleteCmdList = listOf("delete", "remove")

        val suggestions: MutableList<String> = ArrayList()
        if (args.isEmpty() || args.size == 1) {
            val subCommand = if (args.isEmpty()) "" else args[0]

            val commands = mutableListOf<String>()

            if (sender.hasPermission("eternalreports.help"))
                commands.add("help")

            if (sender.hasPermission("eternalreports.reload"))
                commands.add("reload")

            if (sender.hasPermission("eternalreports.menu"))
                commands.add("menu")

            if (sender.hasPermission("eternalreports.resolve"))
                commands.add("resolve")

            if (sender.hasPermission("eternalreports.delete")) {
                commands.add("remove")
                commands.add("delete")
            }

            if (sender.hasPermission("eternalreports.toggle")) {
                commands.add("alerts")
                commands.add("toggle")
            }

            StringUtil.copyPartialMatches(subCommand, commands, suggestions)
        } else if (args.size == 2) {
            if (args[0].toLowerCase() == "menu" && sender.hasPermission("eternalreports.menu.other")) {
                val players: MutableList<String> = ArrayList()
                Bukkit.getOnlinePlayers().stream().filter { player -> !player.hasPermission("vanished") }.forEach { player -> players.add(player.name) }

                StringUtil.copyPartialMatches(args[1].toLowerCase(), players, suggestions)

            } else if (args[0].toLowerCase() == "resolve" && sender.hasPermission("eternalreports.resolve") || deleteCmdList.contains(args[0].toLowerCase()) && sender.hasPermission("eternalreports.delete")) {

                val ids: MutableList<String> = ArrayList()
                plugin.getManager(ReportManager::class.java).reports.stream().forEach { t -> ids.add(t.id.toString()) }

                StringUtil.copyPartialMatches(args[1].toLowerCase(), ids, suggestions)
            }
        } else {
            return null
        }
        return suggestions
    }

    override fun addSubCommands() {
        subcommands.addAll(listOf(CmdHelp(plugin as EternalReports, this), CmdMenu(plugin, this), CmdReload(plugin, this), CmdRemove(plugin, this), CmdResolve(plugin, this), CmdToggle(plugin, this)))
    }

}