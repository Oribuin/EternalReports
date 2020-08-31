package xyz.oribuin.eternalreports.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.eternalreports.data.StaffMember
import java.util.function.Predicate

class PlayerJoin : Listener {

    @EventHandler(ignoreCancelled = true)
    fun playerJoin(event: PlayerJoinEvent) {

        val toggleList = StaffMember.instance.toggleList
        if (event.player.hasPermission("eternalreports.alerts")) {

            toggleList.add(event.player.uniqueId)
        } else {
            toggleList.remove(event.player.uniqueId)
        }
    }
}