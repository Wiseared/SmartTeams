package xyz.wiseared.smartdevelopment.teams.user;

import lombok.Getter;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserManager {

    private final Map<UUID, User> users;
    private final SmartTeams plugin;

    public UserManager(SmartTeams plugin) {
        this.plugin = plugin;

        this.users = new HashMap<>();

        loadUsers();
    }

    private void loadUsers() {
        if (plugin.getDataYML().getConfig().getConfigurationSection("USERS") != null) {
            for (String string : plugin.getDataYML().getConfig().getConfigurationSection("USERS").getKeys(false)) {
                users.put(UUID.fromString(string), new User(UUID.fromString(string)));
            }
        }
    }
}