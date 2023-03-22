package xyz.wiseared.smartdevelopment.teams.team.member;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class TeamMember {

    private UUID uuid;

    private TeamRole role;

    public TeamMember(UUID uuid, TeamRole role) {
        this.uuid = uuid;
        this.role = role;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}