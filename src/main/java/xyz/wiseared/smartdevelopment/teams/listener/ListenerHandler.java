package xyz.wiseared.smartdevelopment.teams.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.listener.listeners.ProfileListener;
import xyz.wiseared.smartdevelopment.teams.listener.listeners.TeamListeners;

public class ListenerHandler {

    public ListenerHandler(SmartTeams plugin) {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new ProfileListener(plugin), plugin);
        pm.registerEvents(new TeamListeners(plugin), plugin);
    }
}