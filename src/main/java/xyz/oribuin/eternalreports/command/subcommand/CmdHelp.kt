package xyz.oribuin.eternalreports.command.subcommand

import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.manager.MessageManager
import xyz.oribuin.eternalreports.util.HexUtils
import xyz.oribuin.orilibrary.command.OriCommand
import xyz.oribuin.orilibrary.command.SubCommand

class CmdHelp(private val plugin: EternalReports, command: OriCommand) : SubCommand(command, "help") {
    override fun executeArgument(sender: CommandSender, args: Array<String>) {
        val messageManager = plugin.getManager(MessageManager::class.java)

        // Check permission
        if (!sender.hasPermission("eternalreports.help")) {
            messageManager.sendMessage(sender, "invalid-permission")
            return
        }

        // Send help message
        for (string in messageManager.messageConfig.getStringList("help-message")) {
            sender.sendMessage(HexUtils.colorify(string))
        }

        // Send sound if player
        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_ARROW_HIT_PLAYER, 50f, 1f)
        }
    }

}