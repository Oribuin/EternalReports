package xyz.oribuin.eternalreports.managers

import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.menus.Menu
import xyz.oribuin.eternalreports.menus.ReportsMenu
import java.util.*
import java.util.function.Consumer

class GuiManager(plugin: EternalReports) : Manager(plugin) {
    private val menus = LinkedList<Menu>()

    private fun registerMenus() {
        ReportsMenu.instance?.let { menus.add(it) }
    }

    override fun reload() {
        this.registerMenus()

        menus.forEach(Consumer { menu -> menu.reload() })
    }

    override fun disable() {
        // Unused
    }
}