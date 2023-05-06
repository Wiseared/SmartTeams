package xyz.wiseared.smartdevelopment.teams.command.commands;

import lombok.AllArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Optional;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamMember;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamRole;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.utils.config.Config;
import xyz.wiseared.smartdevelopment.teams.utils.config.Messages;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.Message;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TeamCommands extends Message {

    private final SmartTeams plugin;

    @Command({"team create", "t create"})
    @Description("Create a team command")
    public final void teamCreateCommand(@Sender CommandSender sender, @Name("name") String name) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        if (plugin.getTeamManager().getTeam(name) != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_EXISTS")));
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (user.getTeam() != null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_IN_TEAM")));
            return;
        }

        for (String string : Config.FORBIDDEN_TEAM_NAMES) {
            if (name.equalsIgnoreCase(string)) {
                sender.sendMessage(CC.translate(Messages.TEAM_FORBIDDEN_NAME));
                return;
            }
        }

        Team team = plugin.getTeamManager().createTeam(name);
        team.addPlayer(player, TeamRole.LEADER);
        team.setHome(player.getLocation());
        user.setTeam(team);
        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.MESSAGE")
                .replace("%team%", name)));

        Bukkit.broadcastMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CREATED.BROADCAST")
                .replace("%player%", player.getName())
                .replace("%team%", name)));
    }

    @Command({"team join", "t join"})
    @Description("Join a team command")
    public final void teamJoinCommand(@Sender CommandSender sender, @Name("name") String name) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (user.getTeam() != null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.ALREADY_IN_TEAM")));
            return;
        }

        Team team = plugin.getTeamManager().getTeam(name);

        if (team == null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DOESNT_EXIST")));
            return;
        }

        if (!user.getTeamInvites().contains(team)) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.NOT_INVITED")
                    .replace("%team%", name)));
            return;
        }

        user.getTeamInvites().remove(team);
        user.setTeam(team);

        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.JOINED")
                .replace("%team%", team.getName())));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.JOIN.BROADCAST")
                .replace("%player%", player.getName()));

        team.addPlayer(player, TeamRole.MEMBER);
    }

    @Command({"team disband", "t disband"})
    @Description("Disband a team command")
    public final void teamDisbandCommand(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
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
    }

    @Command({"team leave", "t leave"})
    @Description("Leave a team command")
    public final void teamLeaveCommand(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (user.getTeam() == null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (team.getRole(player) == TeamRole.LEADER) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.DISBAND.DISBANDED")));
            team.disband();
            return;
        }

        user.setTeam(null);
        team.removePlayer(player);

        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.LEAVE.LEFT")
                .replace("%team%", team.getName())));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.LEAVE.BROADCAST")
                .replace("%player%", player.getName()));
    }

    @Command({"team invite", "t invite"})
    @Description("Invite a player command")
    public final void teamInviteCommand(@Sender CommandSender sender, @Name("player") Player target) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());
        User targetUser = plugin.getUserManager().getUsers().get(target.getUniqueId());

        if (user.getTeam() == null) {
            sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (!canInvite(team.getTeamMember(player))) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.NO_PERMISSION")));
            return;
        }

        if (targetUser.getTeam() != null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.ALREADY_HAS_A_TEAM")));
            return;
        }

        if (targetUser.getTeamInvites().contains(user.getTeam())) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.ALREADY_INVITED")));
            return;
        }

        targetUser.getTeamInvites().add(team);
        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.INVITE_SENT")
                .replace("%player%", target.getName())));
        target.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.INVITE.INVITE_RECEIVED")
                .replace("%team%", team.getName())));
    }

    @Command({"team home", "t home"})
    @Description("Teleport to your team's home")
    public final void teamHomeCommand(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (user.getTeam() == null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        player.teleport(team.getHome());
        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.TELEPORTED")
                .replace("%team%", team.getName())));
    }

    @Command({"team info", "t info"})
    @Description("Get information on a team")
    public final void teamInfoCommand(@Sender CommandSender sender, @Name("team") @Optional Team team) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (team == null) {
            if (user.getTeam() == null) {
                sender.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
                return;
            }

            Team playerTeam = user.getTeam();

            List<String> member = team.getMembers().stream()
                    .filter(m -> m.getRole() == TeamRole.MEMBER || m.getRole() == TeamRole.CAPTAIN || m.getRole() == TeamRole.CO_LEADER)
                    .map(m -> m.getPlayer().getName())
                    .collect(Collectors.toList());

            plugin.getMessagesYML().getConfig().getStringList("TEAMS.INFO").forEach(s -> sender.sendMessage(CC.translate(s
                    .replace("%members%", StringUtils.join(member, ", "))
                    .replace("%z%", String.valueOf(playerTeam.getHome().getBlockZ()))
                    .replace("%y%", String.valueOf(playerTeam.getHome().getBlockY()))
                    .replace("%x%", String.valueOf(playerTeam.getHome().getBlockX()))
                    .replace("%leader%", playerTeam.getLeader().getName())
                    .replace("%team%", playerTeam.getName()))));
            return;
        }

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

    @Command({"team kick", "t kick"})
    @Description("Kick someone from your team")
    public final void teamKickCommand(@Sender CommandSender sender, @Name("player") Player target) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());
        User targetUser = plugin.getUserManager().getUsers().get(target.getUniqueId());

        if (user.getTeam() == null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        if (team != targetUser.getTeam()) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.NOT_IN_TEAM")));
            return;
        }

        if (!team.canKick(player, target)) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.CANT_KICK")));
            return;
        }

        team.kick(target);
        targetUser.setTeam(null);

        team.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.KICK")
                .replace("%team%", team.getName())));
        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.KICKER")
                .replace("%player%", target.getName())));

        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.KICK.BROADCAST")
                .replace("%player%", target.getName()));
    }

    @Command({"team sethome", "t sethome"})
    @Description("Set your team's home")
    public final void teamSetHomeCommand(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
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
            sendMessage(player, plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.NOT_PERMITTED"));
        }
    }

    @Command({"team manage", "t manage"})
    @Description("Manage your team thru an gui")
    public final void teamManageCommand(@Sender CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOnlyPlayer(sender);
            return;
        }

        Player player = (Player) sender;
        User user = plugin.getUserManager().getUsers().get(player.getUniqueId());

        if (user.getTeam() == null) {
            player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.NO_TEAM")));
            return;
        }

        Team team = user.getTeam();

        team.setHome(player.getLocation());
        player.sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.UPDATED")));
        team.sendMessage(plugin.getMessagesYML().getConfig().getString("TEAMS.HOME.BROADCAST")
                .replace("%player%", player.getName()));
    }

    public boolean canInvite(TeamMember member) {
        return member.getRole() == TeamRole.LEADER || member.getRole() == TeamRole.CAPTAIN || member.getRole() == TeamRole.CO_LEADER;
    }
}