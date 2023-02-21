package xyz.wiseared.smartdevelopment.teams;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.wiseared.smartdevelopment.teams.command.CommandHandler;
import xyz.wiseared.smartdevelopment.teams.listener.ListenerHandler;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.team.TeamManager;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.user.UserManager;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.YamlDoc;

@Getter
public class SmartTeams extends JavaPlugin {

    private UserManager userManager;
    private TeamManager teamManager;

    private YamlDoc dataYML;
    private YamlDoc messagesYML;

    public static SmartTeams getInstance() {
        return getPlugin(SmartTeams.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        CC.console(CC.CHAT_BAR);

        setup();

        CC.console(CC.CHAT_BAR);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        for (User user : getUserManager().getUsers().values()) {
            user.save();
        }

        for (Team team : getTeamManager().getTeams().values()) {
            team.save();
        }

        getDataYML().save();
    }

    @SneakyThrows
    private void setup() {
        dataYML = new YamlDoc(getDataFolder(), "data.yml");
        dataYML.init();

        messagesYML = new YamlDoc(getDataFolder(), "messages.yml");
        messagesYML.init();

        teamManager = new TeamManager(this);
        userManager = new UserManager(this);

        new ListenerHandler(this);
        new CommandHandler(this);
    }
}