package xyz.oribuin.eternalreports.events

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import xyz.oribuin.eternalreports.data.Report

class ReportDeleteEvent(val report: Report) : Event(), Cancellable {
    private var isCancelled = false

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(b: Boolean) {
        isCancelled = b
    }

    companion object {
        val handlerList = HandlerList()
    }
}