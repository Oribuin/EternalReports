package xyz.oribuin.eternalreports.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerReportEvent(
        /**
         * This returns the player who reported the other player
         *
         * @return Command Sender
         */
        val sender: Player, reported: Player,

        /**
         * The reason on why the player was reported
         *
         * @return Report Reason
         */
        val reason: String,

        /**
         * Check if report was resolved.
         *
         * @return true if reported is resolved
         */

        var isResolved: Boolean) : PlayerEvent(reported), Cancellable {

    private var cancelled = false


    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    companion object {
        @JvmStatic
        private val handlers = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlers
    }

}