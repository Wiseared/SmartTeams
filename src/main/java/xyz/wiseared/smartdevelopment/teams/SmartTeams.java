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
import xyz.wiseared.smartdevelopment.teams.utils.config.Config;
import xyz.wiseared.smartdevelopment.teams.utils.config.Messages;
import xyz.wiseared.smartdevelopment.teams.utils.menu.MenuHandler;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.YamlDoc;

@Getter
public class SmartTeams extends JavaPlugin {

    private UserManager userManager;
    private TeamManager teamManager;

    private YamlDoc dataYML;
    private YamlDoc messagesYML;
    private YamlDoc menusYML;

    public static SmartTeams getInstance() {
        return getPlugin(SmartTeams.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        CC.console(CC.CHAT_BAR);

        setup();

        CC.console("&7[&5SmartTeams&7] &aEnabled");
        CC.console("&7[&5SmartTeams&7] &7By: &dWiseared");
        CC.console("&7[&5SmartTeams&7] &ddiscord.smartdevelopment.tech");

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

        menusYML = new YamlDoc(getDataFolder(), "menus.yml");
        menusYML.init();

        teamManager = new TeamManager(this);
        userManager = new UserManager(this);

        Messages.init();
        Config.init();
        Menus.init();

        new ListenerHandler(this);
        new CommandHandler(this);
        new MenuHandler(this);
    }
}