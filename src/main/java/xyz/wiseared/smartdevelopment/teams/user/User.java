package xyz.wiseared.smartdevelopment.teams.user;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;

import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID uuid;
    private String name;

    private Team team;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.team = null;
    }

    public User(UUID uuid) {
        this.uuid = uuid;
        load();
    }

    public void load() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "USERS." + uuid.toString() + ".";

        this.name = config.getString(path + "NAME");

        if (config.getString(path + "TEAM") != null) {
            this.team = SmartTeams.getInstance().getTeamManager().getTeams().get(UUID.fromString(config.getString(path + "TEAM")));
        }
    }

    public void save() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "USERS." + uuid.toString() + ".";

        config.set(path + "NAME", name);

        if (team != null) {
            config.set(path + "TEAM", team.getUuid());
        }
    }
}