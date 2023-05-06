package xyz.wiseared.smartdevelopment.teams.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;

import java.util.List;

public class Config {

    public static List<String> FORBIDDEN_TEAM_NAMES;

    public static void init() {
        FileConfiguration config = SmartTeams.getInstance().getConfig();

        FORBIDDEN_TEAM_NAMES = config.getStringList("TEAMS.FORBIDDEN_NAMES");
    }
}