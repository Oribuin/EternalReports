package xyz.oribuin.eternalreports.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.menus.ReportsMenu

class CmdReports(override val plugin: EternalReports) : OriCommand(plugin, "reports") {

    override fun executeCommand(sender: CommandSender, args: Array<String>) {
        if (sender !is Player)
            return

        ReportsMenu(sender).openGui()

    }

    override fun tabComplete(sender: CommandSender, args: Array<String>): List<String>? {
        return null
    }
}