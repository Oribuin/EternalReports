package xyz.oribuin.eternalreports.command

import org.bukkit.command.CommandSender

abstract class SubCommand(private val oriCommand: OriCommand, vararg val names: String) {

    abstract fun executeArgument(sender: CommandSender, args: Array<String>)
}