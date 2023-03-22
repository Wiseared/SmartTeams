package xyz.wiseared.smartdevelopment.teams.command.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamMember;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamRole;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.Command;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.parameter.Param;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

import java.util.List;
import java.util.stream.Collectors;

public class TeamCommands {

    private final SmartTeams plugin;

    public TeamCommands(SmartTeams plugin) {
        this.plugin = plugin;
    }

    @Command(labels = {"team create", "t create"}, description = "Create a team.")
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
        team.addPlayer(sender, TeamRole.LEADER);
        team.setHome(sender.getLocation());
        user.setTeam(team);
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.MESSAGE")
                .replace("%team%", name)));

        Bukkit.broadcastMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.BROADCAST")
                .replace("%player%", sender.getName())
                .replace("%team%", name)));
    }

    @Command(labels = {"team join", "t join"}, description = "Join a team.")
    public void teamJoinCommand(Player sender, @Param("team") String name) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_IN_TEAM")));
            return;
        }

        Team team = plugin.getTeamManager().getTeam(name);

        if (team == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DOESNT_EXIST")));
            return;
        }

        if (!user.getTeamInvites().contains(team)) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.NOT_INVITED")
                    .replace("%team%", name)));
            return;
        }

        user.getTeamInvites().remove(team);
        user.setTeam(team);

        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.JOINED")
                .replace("%team%", team.getName())));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.BROADCAST")
                .replace("%player%", sender.getName()));

        team.addPlayer(sender, TeamRole.MEMBER);
    }

    @Command(labels = {"team disband", "t disband"}, description = "Disband your team.")
    public void teamDisbandCommand(Player sender) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (team.getRole(sender) != TeamRole.LEADER) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.LEADER")));
            return;
        }

        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.DISBANDED")));
        team.disband();
    }

    @Command(labels = {"team leave", "t leave"}, description = "Leave a team.")
    public void teamLeaveCommand(Player sender) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (team.getRole(sender) == TeamRole.LEADER) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.DISBANDED")));
            team.disband();
            return;
        }

        user.setTeam(null);
        team.removePlayer(sender);

        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.LEAVE.LEFT")
                .replace("%team%", team.getName())));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.LEAVE.BROADCAST")
                .replace("%player%", sender.getName()));
    }

    @Command(labels = {"team invite", "t invite"}, description = "Invite someone to your team.")
    public void teamInviteCommand(Player sender, @Param("target") Player player2) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());
        User target = plugin.getUserManager().getUsers().get(player2.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (!canInvite(team.getTeamMember(sender))) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.NO_PERMISSION")));
            return;
        }

        if (target.getTeam() != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.ALREADY_HAS_A_TEAM")));
            return;
        }

        if (target.getTeamInvites().contains(user.getTeam())) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.ALREADY_INVITED")));
            return;
        }

        target.getTeamInvites().add(team);
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.INVITE_SENT")
                .replace("%player%", player2.getName())));
        player2.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.INVITE_RECEIVED")
                .replace("%team%", team.getName())));
    }

    @Command(labels = {"team home", "t home"}, description = "Teleport to your team's home.")
    public void teamHomeCommand(Player sender) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        sender.teleport(team.getHome());
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.TELEPORTED")
                .replace("%team%", team.getName())));
    }

    @Command(labels = {"team info", "t info"}, description = "Get team info.")
    public void teamInfoCommand(Player sender) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        List<String> member = team.getMembers().stream()
                .filter(m -> m.getRole() == TeamRole.MEMBER || m.getRole() == TeamRole.CAPTAIN || m.getRole() == TeamRole.CO_LEADER)
                .map(m -> m.getPlayer().getName())
                .collect(Collectors.toList());

        plugin.getMessagesYML().getConfig().getStringList("TEAMS.INFO").forEach(s -> sender.sendMessage(CC.translate(s
                .replace("%members%", StringUtils.join(member, ", "))
                .replace("%z%", String.valueOf(team.getHome().getBlockZ()))
                .replace("%y%", String.valueOf(team.getHome().getBlockY()))
                .replace("%x%", String.valueOf(team.getHome().getBlockX()))
                .replace("%leader%", team.getLeader().getName())
                .replace("%team%", team.getName()))));
    }

    @Command(labels = {"team kick", "t kick"}, description = "Kick someone from your team.")
    public void teamKickCommand(Player sender, @Param("target") Player target) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());
        User player = plugin.getUserManager().getUsers().get(target.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (team != player.getTeam()) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.NOT_IN_TEAM")));
            return;
        }

        if (!team.canKick(sender, target)) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.CANT_KICK")));
            return;
        }

        team.kick(target);
        player.setTeam(null);

        team.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.KICK")
                .replace("%team%", team.getName())));
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.KICKER")
                .replace("%player%", target.getName())));

        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.BROADCAST")
                .replace("%player%", target.getName()));
    }

    @Command(labels = {"team sethome", "t sethome"}, description = "Set your team's home.")
    public void teamSetHomeCommand(Player sender) {
        User user = plugin.getUserManager().getUsers().get(sender.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        team.setHome(sender.getLocation());
        sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.UPDATED")));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.BROADCAST")
                .replace("%player%", sender.getName()));
    }

    public boolean canInvite(TeamMember member) {
        return member.getRole() == TeamRole.LEADER || member.getRole() == TeamRole.CAPTAIN || member.getRole() == TeamRole.CO_LEADER;
    }
}