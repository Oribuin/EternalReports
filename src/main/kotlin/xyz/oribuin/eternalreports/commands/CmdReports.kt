package xyz.oribuin.eternalreports.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.menus.ReportsMenu

class CmdReports(override val plugin: EternalReports) : OriCommand(plugin, "reports") {

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.messageManager;
        if (args.isEmpty() || args.size == 1 && args[0].toLowerCase() == "menu") {
            if (sender !is Player) {
                messageManager.sendMessage(sender, "player-only")
                return
            }

            if (!sender.hasPermission("eternalreports.menu")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            ReportsMenu(sender).openGui()
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

            ReportsMenu(mentioned).openGui()
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}