package xyz.oribuin.eternalreports.managers

import xyz.oribuin.eternalreports.EternalReports
import xyz.oribuin.eternalreports.menus.Menu
import xyz.oribuin.eternalreports.menus.ReportsMenu
import java.util.*
import java.util.function.Consumer

class GuiManager(plugin: EternalReports) : Manager(plugin) {
    private val menus = LinkedList<Menu>()


    fun registerMenus() {
        ReportsMenu.instance?.let { menus.add(it) }
    }

    override fun reload() {
        menus.forEach(Consumer { obj: Menu -> obj.reload() })
    }
}