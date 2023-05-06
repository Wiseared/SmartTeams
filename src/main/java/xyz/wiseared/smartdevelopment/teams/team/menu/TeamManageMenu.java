package xyz.wiseared.smartdevelopment.teams.team.menu;

import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamRole;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.utils.config.Menus;
import xyz.wiseared.smartdevelopment.teams.utils.menu.Menu;
import xyz.wiseared.smartdevelopment.teams.utils.menu.buttons.Button;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

import java.util.HashMap;
import java.util.Map;

public class TeamManageMenu extends Menu {

    private final SmartTeams plugin;

    public TeamManageMenu(Player player, SmartTeams plugin) {
        super(player, CC.translate(Menus.TEAM_MANAGE_TITLE), Menus.TEAM_MANAGE_SIZE * 9);
        this.plugin = plugin;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(Menus.TEAM_MANAGE_MEMBER_MANAGE_SLOT, new Button(Menus.TEAM_MANAGE_MEMBER_MANAGE)
                .setClickAction(event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    new TeamMemberManageMenu(player, plugin).updateMenu();
        }));

        buttons.put(Menus.TEAM_MANAGE_SET_HOME_SLOT, new Button(Menus.TEAM_MANAGE_SET_HOME)
                .setClickAction(event -> {
                    event.setCancelled(true);
                    player.closeInventory();

                    User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

                    if (user.getTeam() == null) {
                        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
                        return;
                    }

                    Team team = user.getTeam();

                    if (team.getRole(player) == TeamRole.LEADER || team.getRole(player) == TeamRole.CO_LEADER) {
                        team.setHome(player.getLocation());
                        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.UPDATED")));
                        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.BROADCAST")
                                .replace("%player%", player.getName()));
                    } else {
                        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.NOT_PERMITTED")));
                    }
                }));

        buttons.put(Menus.TEAM_MANAGE_DISBAND_SLOT, new Button(Menus.TEAM_MANAGE_DISBAND)
                .setClickAction(event -> {
                    player.closeInventory();
                    event.setCancelled(true);

                    User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

                    if (user.getTeam() == null) {
                        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
                        return;
                    }

                    Team team = user.getTeam();

                    if (team.getRole(player) != TeamRole.LEADER) {
                        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.LEADER")));
                        return;
                    }

                    player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.DISBANDED")));
                    team.disband();
                }));

        return buttons;
    }
}