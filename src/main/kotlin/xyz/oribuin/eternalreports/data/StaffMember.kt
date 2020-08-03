package xyz.oribuin.eternalreports.data

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class StaffMember(private val player: Player) {
    val toggleList: MutableMap<UUID, Boolean>

    init {
        toggleList = HashMap()
    }

    fun hasNotifications(): Boolean {
        return toggleList.getOrDefault(player.uniqueId, true)
    }
}