package xyz.wiseared.smartdevelopment.teams.user;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID uuid;
    private String name;

    private Team team;

    private List<Team> teamInvites;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.team = null;

        this.teamInvites = new ArrayList<>();
    }

    public User(UUID uuid) {
        this.uuid = uuid;

        this.teamInvites = new ArrayList<>();

        load();
    }

    public void load() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "USERS." + uuid.toString() + ".";

        this.name = config.getString(path + "NAME");

        if (config.getString(path + "TEAM") != null) {
            this.team = SmartTeams.getInstance().getTeamManager().getTeams().get(UUID.fromString(config.getString(path + "TEAM")));
        }

        if (config.getConfigurationSection(path + "INVITES") != null) {
            for (String string : config.getConfigurationSection(path + "INVITES").getKeys(false)) {
                teamInvites.add(SmartTeams.getInstance().getTeamManager().getTeam(UUID.fromString(string)));
            }
        }
    }

    public void save() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "USERS." + uuid.toString() + ".";

        config.set(path + "NAME", name);

        if (team != null) {
            config.set(path + "TEAM", team.getUuid().toString());
        }
    }
}