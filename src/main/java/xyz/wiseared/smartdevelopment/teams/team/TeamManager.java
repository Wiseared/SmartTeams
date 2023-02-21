package xyz.wiseared.smartdevelopment.teams.team;

import lombok.Getter;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;

import java.util.Map;
import java.util.UUID;

@Getter
public class TeamManager {

    public Map<UUID, Team> teams;
    private final SmartTeams plugin;

    public TeamManager(SmartTeams plugin) {
        this.plugin = plugin;

        loadTeams();
    }

    public Team createTeam(String name) {
        UUID uuid = UUID.randomUUID();
        Team team = new Team(name, uuid);
        teams.put(uuid, team);

        return team;
    }

    public void deleteTeam(UUID uuid) {
        SmartTeams.getInstance().getDataYML().getConfig().set("TEAMS." + uuid.toString(), null);
        teams.remove(uuid);
    }

    public Team getTeam(String name) {
        return teams.values().stream().filter(team -> team.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void loadTeams() {
        if (plugin.getDataYML().getConfig().getConfigurationSection("TEAMS") != null) {
            for (String string : plugin.getDataYML().getConfig().getConfigurationSection("TEAMS").getKeys(false)) {
                teams.put(UUID.fromString(string), new Team(UUID.fromString(string)));
            }
        }
    }
}