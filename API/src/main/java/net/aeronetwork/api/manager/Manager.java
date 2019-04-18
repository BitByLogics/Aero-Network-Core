package net.aeronetwork.api.manager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Manager implements Listener, ManagerInterface {

    private String name;
    private String description;

    public Manager(String name, String description, JavaPlugin plugin) {
        this.name = name;
        this.description = description;
        plugin.getServer().getPluginManager().registerEvents(this, plugin); //Managers are automatically registered.
        preEnable();
        onEnable();
    }

    @Override
    public void preEnable() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}