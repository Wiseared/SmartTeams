package xyz.wiseared.smartdevelopment.teams.command;

import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.command.commands.TeamCommands;
import xyz.wiseared.smartdevelopment.teams.utils.command.Zetsu;

import java.util.Arrays;

public class CommandHandler {

    public CommandHandler(SmartTeams plugin) {

        Zetsu zetsu = new Zetsu(plugin);

        Arrays.asList(
                new TeamCommands(plugin)
        ).forEach(zetsu::registerCommands);
    }
}