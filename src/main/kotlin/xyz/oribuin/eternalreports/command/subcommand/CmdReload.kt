package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.command.CommandSender
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.orilibrary.OriCommand
import xyz.oribuin.orilibrary.StringPlaceholders
import xyz.oribuin.orilibrary.SubCommand

class CmdReload(private val plugin: EternalReports, command: OriCommand) : SubCommand(command, "reload") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        if (!sender.hasPermission("eternalreports.reload")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        this.plugin.reload()
        messageManager.sendMessage(sender, "reload", StringPlaceholders.single("version", this.plugin.description.version))
    }
}