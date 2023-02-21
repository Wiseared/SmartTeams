package xyz.wiseared.smartdevelopment.teams.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;

import java.util.UUID;

@Getter
@Setter
public class Team {

    private String name;
    private UUID uuid;

    public Team(UUID uuid) {
        this.uuid = uuid;

        load();
    }

    public Team(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;
    }

    public void load() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "TEAMS." + uuid.toString() + ".";

        this.name = config.getString(path + "NAME");
    }

    public void save() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "TEAMS." + uuid.toString() + ".";

        config.set(path + "NAME", name);
    }
}