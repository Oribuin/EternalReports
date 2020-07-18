package xyz.oribuin.eternalreports.utils

import org.bukkit.Bukkit

object NMSUtil {
    private var cachedVersion: String? = null
    private var cachedVersionNumber = -1

    /**
     * Gets the server version
     *
     * @return The server version
     */
    val version: String?
        get() {
            if (cachedVersion == null) {
                val name = Bukkit.getServer().javaClass.getPackage().name
                cachedVersion = name.substring(name.lastIndexOf('.') + 1) + "."
            }
            return cachedVersion
        }

    /**
     * Gets the server version major release number
     *
     * @return The server version major release number
     */
    @JvmStatic
    val versionNumber: Int
        get() {
            if (cachedVersionNumber == -1) {
                val name = version!!.substring(3)
                cachedVersionNumber = name.substring(0, name.length - 4).toInt()
            }
            return cachedVersionNumber
        }

    /**
     * @return true if the server is running Spigot or a fork, false otherwise
     */
    val isSpigot: Boolean
        get() = try {
            Class.forName("org.spigotmc.SpigotConfig")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
}