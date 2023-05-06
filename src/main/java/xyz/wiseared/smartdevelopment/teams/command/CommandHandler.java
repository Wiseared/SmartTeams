package xyz.wiseared.smartdevelopment.teams.command;

import me.vaperion.blade.Blade;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.command.commands.SmartTeamsCommand;
import xyz.wiseared.smartdevelopment.teams.command.commands.TeamCommands;
import xyz.wiseared.smartdevelopment.teams.command.provider.TeamProvider;
import xyz.wiseared.smartdevelopment.teams.team.Team;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

public class CommandHandler {

    public CommandHandler(SmartTeams plugin) {

        Blade.forPlatform(new BladeBukkitPlatform(plugin))
                .config(cfg -> {
                    cfg.setOverrideCommands(true);
                    cfg.setFallbackPrefix("smartteams");
                    cfg.setDefaultPermissionMessage(CC.translate("&cNo permission!"));
                })
                .bind(binder -> {
                    binder.bind(Team.class, new TeamProvider(plugin));
                })
                .build()
                .register(new SmartTeamsCommand(plugin))
                .register(new TeamCommands(plugin));
    }
}