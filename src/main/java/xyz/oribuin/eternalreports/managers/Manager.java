package xyz.oribuin.eternalreports.managers;

import xyz.oribuin.eternalreports.EternalReports;

public abstract class Manager {

    protected final EternalReports plugin;

    public Manager(EternalReports plugin) {
        this.plugin = plugin;
    }

    public abstract void reload();

}
