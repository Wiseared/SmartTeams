package xyz.wiseared.smartdevelopment.teams.command.provider;

import lombok.AllArgsConstructor;
import me.vaperion.blade.argument.Argument;
import me.vaperion.blade.argument.ArgumentProvider;
import me.vaperion.blade.context.Context;
import me.vaperion.blade.exception.BladeExitMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TeamProvider implements ArgumentProvider<Team> {

    private final SmartTeams plugin;

    @Override
    public @Nullable Team provide(@NotNull Context context, @NotNull Argument argument) throws BladeExitMessage {
        Team team = plugin.getTeamManager().getTeam(argument.getString());
        if (team == null) {
            throw new BladeExitMessage(CC.translate("&cThis team was not found!"));
        }
        return team;
    }

    @Override
    public @NotNull List<String> suggest(@NotNull Context ctx, @NotNull Argument arg) throws BladeExitMessage {
        return plugin.getTeamManager().getTeams().values().stream().map(Team::getName).collect(Collectors.toList());
    }
}