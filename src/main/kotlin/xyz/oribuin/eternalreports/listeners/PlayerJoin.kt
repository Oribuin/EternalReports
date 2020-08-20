package xyz.oribuin.eternalreports.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.eternalreports.data.StaffMember
import java.util.function.Predicate

class PlayerJoin : Listener {

    @EventHandler(ignoreCancelled = true)
    fun playerJoin(event: PlayerJoinEvent) {

        val staffPlayer = StaffMember(event.player)
        if (event.player.hasPermission("eternalreports.alerts")) {

            staffPlayer.toggleList.add(event.player.uniqueId)
        } else {
            staffPlayer.toggleList.remove(event.player.uniqueId)
        }
    }
}