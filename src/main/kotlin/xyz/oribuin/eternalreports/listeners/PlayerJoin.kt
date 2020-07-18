package xyz.oribuin.eternalreports.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.oribuin.eternalreports.data.StaffPlayer

class PlayerJoin : Listener {

    @EventHandler(ignoreCancelled = true)
    fun playerJoin(event: PlayerJoinEvent) {
        val staffPlayer = StaffPlayer(event.player)
        if (event.player.hasPermission("eternalreports.alerts")) {

            staffPlayer.toggleList.putIfAbsent(event.player.uniqueId, true)
        } else {
            staffPlayer.toggleList.putIfAbsent(event.player.uniqueId, false)
        }
    }
}