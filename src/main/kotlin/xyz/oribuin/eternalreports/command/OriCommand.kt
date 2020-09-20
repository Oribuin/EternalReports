package xyz.oribuin.eternalreports.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.event.Listener
import xyz.oribuin.eternalreports.EternalReports

abstract class OriCommand(open val plugin: EternalReports, private val name: String) : TabExecutor, Listener {
    fun register() {
        val cmd = Bukkit.getPluginCommand(name)
        if (cmd != null) {
            cmd.setExecutor(this)
            cmd.tabCompleter = this
        }

        Bukkit.getPluginManager().registerEvents(this, plugin)
        this.addSubCommands()
    }

    abstract fun executeCommand(sender: CommandSender, args: Array<String>)

    abstract fun tabComplete(sender: CommandSender, args: Array<String>): List<String>?

    abstract fun addSubCommands()


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