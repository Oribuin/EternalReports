package xyz.oribuin.eternalreports.managers;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.oribuin.eternalreports.EternalReports;

import java.util.List;

public class ConfigManager extends Manager {
    public ConfigManager(EternalReports plugin) {
        super(plugin);
    }


    @Override
    public void reload() {
        this.plugin.reloadConfig();
        this.plugin.saveDefaultConfig();

        FileConfiguration config = this.plugin.getConfig();
        for (Setting value : Setting.values()) {
            config.addDefault(value.key, value);
            value.load(config);
        }
    }

    public enum Setting {

        COUNTDOWN("countdown");

        private final String key;
        private Object value = null;

        Setting(String key) {
            this.key = key;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            return (String) this.value;
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            return (List<String>) this.value;
        }

        /**
         * Loads the value from the config and caches it
         */
        public void load(FileConfiguration config) {
            this.value = config.get(this.key);
        }

    }

}
