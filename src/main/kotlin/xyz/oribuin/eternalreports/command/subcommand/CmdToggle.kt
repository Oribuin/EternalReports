package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.command.OriCommand
import xyz.oribuin.eternalreports.command.SubCommand
import xyz.oribuin.eternalreports.manager.MessageManager

class CmdToggle(private val plugin: EternalReports, private val command: OriCommand) : SubCommand(command, "toggle", "alerts") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class)

        // Check if player
        if (sender !is Player) {
            messageManager.sendMessage(sender, "player-only")
            return
        }

        // Check permissions
        if (!sender.hasPermission("eternalreports.toggle")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        // Define toggle list
        val toggleList = plugin.toggleList

        // Check if they have notifications on
        if (toggleList.contains(sender.uniqueId)) {

            // Turn off notifications
            toggleList.remove(sender.uniqueId)
            messageManager.sendMessage(sender, "commands.alerts-off")
        } else {

            // Turn on notifications
            toggleList.add(sender.uniqueId)
            messageManager.sendMessage(sender, "commands.alerts-on")
        }

    }

}