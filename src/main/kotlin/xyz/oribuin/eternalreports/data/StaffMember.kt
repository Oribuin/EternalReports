package xyz.oribuin.eternalreports.data

import org.bukkit.entity.Player
import java.util.*

class StaffMember() {

    init {
        instance = this
    }

    val toggleList  = mutableSetOf<UUID>()


    companion object {
        lateinit var instance: StaffMember
    }
}