package xyz.oribuin.eternalreports.data

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class StaffMember(private val player: Player) {
    val toggleList: MutableSet<UUID> = mutableSetOf()

    fun hasNotifications(): Boolean {
        return toggleList.contains(player.uniqueId)
    }
}