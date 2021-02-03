package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.menu.ReportsMenu
import xyz.oribuin.orilibrary.command.OriCommand
import xyz.oribuin.orilibrary.command.SubCommand

class CmdMenu(private val plugin: EternalReports, command: OriCommand) : SubCommand(command, "menu") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        if (args.size == 2) {
            val mentioned = Bukkit.getPlayer(args[1])

            // Check Permission
            if (!sender.hasPermission("eternalreports.menu.other")) {
                messageManager.sendMessage(sender, "invalid-permission")
                return
            }

            // Check if player doesnt exist.
            if (mentioned == null || !mentioned.isOnline || mentioned.hasMetadata("vanished")) {
                messageManager.sendMessage(sender, "invalid-player")
                return
            }

            // Open Menu
            ReportsMenu(plugin).openGui(listOf(mentioned))
            return
        }


        // Check if player
        if (sender !is Player) {
            messageManager.sendMessage(sender, "player-only")
            return
        }

        // Check permission
        if (!sender.hasPermission("eternalreports.menu")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

//        // Open Menu
        ReportsMenu(plugin).openGui(listOf(sender))
    }

}