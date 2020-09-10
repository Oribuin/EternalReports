package xyz.oribuin.eternalreports.manager

import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.menu.Menu
import xyz.oribuin.eternalreports.menu.ReportsMenu
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