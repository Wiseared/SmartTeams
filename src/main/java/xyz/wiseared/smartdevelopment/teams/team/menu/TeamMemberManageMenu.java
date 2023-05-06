package xyz.wiseared.smartdevelopment.teams.team.menu;

import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.utils.config.Menus;
import xyz.wiseared.smartdevelopment.teams.utils.menu.Menu;
import xyz.wiseared.smartdevelopment.teams.utils.menu.buttons.Button;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

import java.util.Map;

public class TeamMemberManageMenu extends Menu {

    private final SmartTeams plugin;

    public TeamMemberManageMenu(Player player, SmartTeams plugin) {
        super(player, CC.translate(Menus.TEAM_MANAGE_TITLE), Menus.TEAM_MANAGE_SIZE * 9);
        this.plugin = plugin;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        return null;
    }
}