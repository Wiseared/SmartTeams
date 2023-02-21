package xyz.wiseared.smartdevelopment.teams.listener.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.user.User;

public class ProfileListener implements Listener {

    private final SmartTeams plugin;

    public ProfileListener(SmartTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getUserManager().getUsers().get(event.getUniqueId()) == null) {
            plugin.getUserManager().getUsers().put(event.getUniqueId(), new User(event.getUniqueId(), event.getName()));
        }
    }
}