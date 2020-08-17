package xyz.oribuin.eternalreports

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import xyz.oribuin.eternalreports.commands.CmdReport
import xyz.oribuin.eternalreports.commands.CmdReports
import xyz.oribuin.eternalreports.commands.OriCommand
import xyz.oribuin.eternalreports.hooks.PlaceholderExp
import xyz.oribuin.eternalreports.listeners.PlayerJoin
import xyz.oribuin.eternalreports.managers.*
import xyz.oribuin.eternalreports.utils.FileUtils
import xyz.oribuin.eternalreports.utils.PluginUtils
import kotlin.reflect.KClass

/**
 * @author Oribuin
 */

class EternalReports : JavaPlugin(), Listener {
    private val managers: MutableMap<KClass<out Manager>, Manager> = HashMap()

    override fun onEnable() {

        // Register all the commands

        PluginUtils.debug("Loading Managers.")
        this.getManager(ConfigManager::class)
        this.getManager(DataManager::class)
        this.getManager(GuiManager::class)
        this.getManager(MessageManager::class)
        this.getManager(ReportManager::class)

        FileUtils.createMenuFile(this, "report-menu")


        PluginUtils.debug("Registering all plugin commands.")
        registerCommands(CmdReport(this), CmdReports(this))

        // Register all the listeners
        PluginUtils.debug("Registering all plugin listeners.")
        registerListeners(PlayerJoin())

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PluginUtils.debug("Registering PlaceholderAPI Placeholders.")
            PlaceholderExp(this).register()
        }

        // Register other stuff
        this.reload()
        this.saveDefaultConfig()
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

    fun <M : Manager> getManager(managerClass: KClass<M>): M {
        synchronized(this.managers) {
            @Suppress("UNCHECKED_CAST")
            if (this.managers.containsKey(managerClass))
                return this.managers[managerClass] as M

            return try {
                val manager = managerClass.constructors.first().call(this)
                manager.reload()
                this.managers[managerClass] = manager
                manager
            } catch (ex: ReflectiveOperationException) {
                error("Failed to load manager for ${managerClass.simpleName}")
            }
        }
    }

    fun reload() {
        this.disableManagers()
        this.managers.values.forEach { manager -> manager.reload() }
    }

    override fun onDisable() {
        this.disableManagers()
    }

    private fun disableManagers() {
        this.managers.values.forEach { manager -> manager.disable() }
    }
}