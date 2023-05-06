package xyz.wiseared.smartdevelopment.teams.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;

import java.util.List;

public class Messages {

    public static List<String> ONLY_PLAYER;

    public static String TEAM_FORBIDDEN_NAME;

    public static void init() {
        FileConfiguration config = SmartTeams.getInstance().getMessagesYML().getConfig();

        ONLY_PLAYER = config.getStringList("GENERAL.ONLY_PLAYER");

        TEAM_FORBIDDEN_NAME = config.getString("TEAMS.FORBIDDEN_NAME");
    }
}