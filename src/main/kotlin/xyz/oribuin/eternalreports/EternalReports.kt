package xyz.oribuin.eternalreports

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import xyz.oribuin.eternalreports.commands.CmdReport
import xyz.oribuin.eternalreports.commands.CmdReports
import xyz.oribuin.eternalreports.commands.OriCommand
import xyz.oribuin.eternalreports.database.DatabaseConnector
import xyz.oribuin.eternalreports.database.SQLiteConnector
import xyz.oribuin.eternalreports.hooks.PlaceholderExp
import xyz.oribuin.eternalreports.listeners.PlayerJoin
import xyz.oribuin.eternalreports.managers.*

/*
  TODO List
   • Create Menus
   • Add report management commands
   • Add filters on GUIs
   • Add MySQL Support
   • Make the plugin functional
 */

class EternalReports : JavaPlugin() {

    lateinit var connector: DatabaseConnector
    lateinit var configManager: ConfigManager
    lateinit var dataManager: DataManager
    lateinit var guiManager: GuiManager
    lateinit var messageManager: MessageManager
    lateinit var reportManager: ReportManager

    companion object {
        var instance: EternalReports? = null
            private set
    }

    override fun onEnable() {
        instance = this

        // Register all the commands
        registerCommands(CmdReport(this), CmdReports(this))

        // Register all the listeners
        registerListeners(PlayerJoin())

        // SQLite
        connector = SQLiteConnector(this)

        // Register Managers
        this.configManager = ConfigManager(this)
        this.dataManager = DataManager(this)
        this.messageManager = MessageManager(this)
        this.guiManager = GuiManager(this)
        this.reportManager = ReportManager(this)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderExp(this).register()
        }

        // Register other stuff
        this.guiManager.registerMenus()
        this.reload()
        this.saveDefaultConfig()
    }

    fun reload() {
        this.configManager.reload()
        this.dataManager.reload()
        this.messageManager.reload()
        this.guiManager.reload()
        this.reportManager.reload()
    }

    private fun registerCommands(vararg commands: OriCommand) {
        for (cmd in commands) {
            cmd.registerCommand()
        }
    }

    private fun registerListeners(vararg listeners: Listener) {
        for (listener in listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this)
        }
    }
}