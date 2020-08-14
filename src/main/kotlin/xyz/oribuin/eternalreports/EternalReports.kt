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
import kotlin.reflect.KClass

/*
  TODO List
   • Create Menus
   • Add report management commands
   • Add filters on GUIs
   • Make the plugin functional
 */

class EternalReports : JavaPlugin(), Listener {


    private val managers: MutableMap<KClass<out Manager>, Manager> = HashMap()

    override fun onEnable() {

        // Load PDM because no one likes large jar files
        /*
        val dependencyManager = PDMBuilder(this).build()
        dependencyManager.loadAllDependencies().join()

         */

        // Register all the commands
        registerCommands(CmdReport(this), CmdReports(this))

        // Register all the listeners
        registerListeners(PlayerJoin())

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderExp(this).register()
        }

        // Register other stuff
        this.reload()
        this.saveDefaultConfig()
    }

    fun reload() {
        this.getManager(ConfigManager::class).reload()
        this.getManager(DataManager::class).reload()
        this.getManager(GuiManager::class).reload()
        this.getManager(MessageManager::class).reload()
        this.getManager(ReportManager::class).reload()
    }

    override fun onDisable() {
        this.disable()
    }

    private fun disable() {
        this.getManager(ConfigManager::class).disable()
        this.getManager(DataManager::class).disable()
        this.getManager(GuiManager::class).disable()
        this.getManager(MessageManager::class).disable()
        this.getManager(ReportManager::class).disable()
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
}