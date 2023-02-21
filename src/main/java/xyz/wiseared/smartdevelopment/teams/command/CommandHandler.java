package xyz.wiseared.smartdevelopment.teams.command;

import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.command.commands.TeamCommand;
import xyz.wiseared.smartdevelopment.teams.utils.command.Zetsu;

import java.util.Arrays;

public class CommandHandler {

    public CommandHandler(SmartTeams plugin) {

        Zetsu zetsu = new Zetsu(plugin);

        Arrays.asList(
                new TeamCommand(plugin)
        ).forEach(zetsu::registerCommands);
    }
}