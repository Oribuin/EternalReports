package xyz.oribuin.eternalreports.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.eternalreports.EternalReports

class PlayerJoin(val plugin: EternalReports) : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun playerJoin(event: PlayerJoinEvent) {

        val toggleList = plugin.toggleList

        if (event.player.hasPermission("eternalreports.alerts")) {
            if (toggleList.contains(event.player.uniqueId))
                return

            toggleList.add(event.player.uniqueId)
        } else {
            toggleList.remove(event.player.uniqueId)
        }
    }
}