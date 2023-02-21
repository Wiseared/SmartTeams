package xyz.wiseared.smartdevelopment.teams.utils.command.adapters.defaults;

import xyz.wiseared.smartdevelopment.warsimulator.utils.command.adapters.ParameterAdapter;
import org.bukkit.command.CommandSender;

public class StringTypeAdapter implements ParameterAdapter<String> {

    @Override
    public String process(String str) {
        return str;
    }

    @Override
    public void processException(CommandSender sender, String given, Exception exception) {
        //Never
    }
}
