package xyz.wiseared.smartdevelopment.teams.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamMember;
import xyz.wiseared.smartdevelopment.teams.team.member.TeamRole;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.LocationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Team {

    private String name;
    private UUID uuid;

    private Location home;

    private List<TeamMember> members;

    public Team(UUID uuid) {
        this.uuid = uuid;
        this.members = new ArrayList<>();

        load();
    }

    public Team(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;

        this.home = null;

        this.members = new ArrayList<>();
    }

    public void addPlayer(Player player, TeamRole role) {
        members.add(new TeamMember(player.getUniqueId(), role));
    }

    public Player getLeader() {
        for (TeamMember member : members) {
            if (member.getRole() == TeamRole.LEADER) {
                return member.getPlayer();
            }
        }
        return null;
    }

    public void removePlayer(Player player) {
        members.remove(getTeamMember(player));

        player.sendMessage(CC.translate(SmartTeams.getInstance().getMessagesYML().getConfig().getString("TEAMS.LEAVE.LEFT")
                .replace("%team%", getName())));
        sendMessage(SmartTeams.getInstance().getMessagesYML().getConfig().getString("TEAMS.LEAVE.BROADCAST")
                .replace("%player%", player.getName()));
    }

    public TeamMember getTeamMember(Player player) {
        return members.stream().filter(teamMember -> teamMember.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    public TeamRole getRole(Player player) {
        return getTeamMember(player).getRole();
    }

    public void sendMessage(String msg) {
        for (TeamMember member : members) {
            if (member.getPlayer() != null) {
                member.getPlayer().sendMessage(CC.translate(msg));
            }
        }
    }

    public void disband() {
        sendMessage(SmartTeams.getInstance().getMessagesYML().getConfig().getString("TEAMS.DISBAND.BROADCAST"));
        SmartTeams.getInstance().getUserManager().getUsers().get(getLeader().getUniqueId()).setTeam(null);
        if (!members.isEmpty() && members.size() > 1) {
            for (TeamMember member : members) {
                member.getPlayer().playSound(member.getPlayer(), Sound.ENTITY_GENERIC_EXPLODE, 3L, 3L);
                SmartTeams.getInstance().getUserManager().getUsers().get(member.getUuid()).setTeam(null);
                members.remove(member);
            }
        }

        SmartTeams.getInstance().getTeamManager().getTeams().remove(uuid);
    }

    public void kick(Player player) {
        members.remove(getTeamMember(player));
    }

    public boolean canKick(Player kicker, Player player) {
        TeamMember kMember = getTeamMember(kicker);
        TeamMember member = getTeamMember(player);

        if (kMember.getRole() == TeamRole.LEADER) return true;
        return kMember.getRole() == TeamRole.CO_LEADER && member.getRole() != TeamRole.CO_LEADER && member.getRole() != TeamRole.LEADER;
    }

    public void load() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "TEAMS." + uuid.toString() + ".";

        this.name = config.getString(path + "NAME");

        if (config.getConfigurationSection(path + "MEMBERS") != null) {
            for (String string : config.getConfigurationSection(path + "MEMBERS").getKeys(false)) {
                members.add(new TeamMember(UUID.fromString(string), TeamRole.valueOf(config.getString(path + "MEMBERS." + string))));
            }
        }

        this.home = LocationUtil.stringToLocation(config.getString(path + "HOME"));
    }

    public void save() {
        FileConfiguration config = SmartTeams.getInstance().getDataYML().getConfig();
        String path = "TEAMS." + uuid.toString() + ".";

        config.set(path + "NAME", name);

        for (TeamMember member : members) {
            config.set(path + "MEMBERS." + member.getUuid().toString(), member.getRole().toString());
        }

        config.set(path + "HOME", LocationUtil.locationToString(home));
    }
}