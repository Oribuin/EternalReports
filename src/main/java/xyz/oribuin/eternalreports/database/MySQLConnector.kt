package xyz.oribuin.eternalreports.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class MySQLConnector(private val plugin: Plugin, hostname: String, port: Int, database: String, username: String, password: String, useSSL: Boolean) : DatabaseConnector {
    private var hikari: HikariDataSource? = null

    override fun closeConnection() {
        hikari?.close()
    }

    override fun connect(callback: (Connection) -> Unit) {
        try {
            hikari?.connection?.use(callback)
        } catch (ex: SQLException) {
            plugin.logger.severe("An error occurred executing a MySQL query: ${ex.message}")
            ex.printStackTrace()
        }
    }

    init {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:mysql://$hostname:$port/$database?useSSL=$useSSL"
        config.username = username
        config.password = password
        config.maximumPoolSize = 5
        try {
            hikari = HikariDataSource(config)
        } catch (ex: SQLException) {
            ex.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
    }
}