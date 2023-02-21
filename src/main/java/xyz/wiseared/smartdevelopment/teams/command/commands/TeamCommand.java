package xyz.wiseared.smartdevelopment.teams.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.Command;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.parameter.Param;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

public class TeamCommand {

    private final SmartTeams plugin;

    public TeamCommand(SmartTeams plugin) {
        this.plugin = plugin;
    }

    @Command(labels = {"team create"}, description = "Create a team.")
    public void teamCreateCommand(Player sender, @Param("name") String name) {
        if (plugin.getTeamManager().getTeam(name) != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_EXISTS")));
            return;
        }

        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_IN_TEAM")));
            return;
        }

        Team team = plugin.getTeamManager().createTeam(name);
        user.setTeam(team);
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.MESSAGE")
                .replace("%team%", name)));

        Bukkit.broadcastMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.BROADCAST")
                .replace("%player%", sender.getName())
                .replace("%team%", name)));
    }
}