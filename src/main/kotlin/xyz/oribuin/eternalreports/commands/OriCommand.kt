package xyz.oribuin.eternalreports.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import xyz.oribuin.eternalreports.EternalReports

abstract class OriCommand(open val plugin: EternalReports, // Get Command Name
                          private val name: String) : TabExecutor {
    fun registerCommand() {
        val cmd = Bukkit.getPluginCommand(name)
        if (cmd != null) {
            cmd.setExecutor(this)
            cmd.tabCompleter = this
        }
    }

    abstract fun executeCommand(sender: CommandSender, args: Array<String>)

    abstract fun tabComplete(sender: CommandSender, args: Array<String>): List<String>?

    // Register Command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        executeCommand(sender, args)
        return true
    }

    // Register command tab complete
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return tabComplete(sender, args)
    }

    val command: Command?
        get() = plugin.getCommand(name)

}