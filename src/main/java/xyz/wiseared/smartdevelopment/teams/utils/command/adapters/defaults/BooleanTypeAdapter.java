package xyz.wiseared.smartdevelopment.teams.utils.command.adapters.defaults;

import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.ParameterAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BooleanTypeAdapter implements ParameterAdapter<Boolean> {

    @Override
    public Boolean process(String str) {
        String lowered = str.toLowerCase();
        if (lowered.equals("yes")) {
            return true;
        } else if (lowered.equals("no")) {
            return false;
        }

        return lowered.equals("true");
    }

    @Override
    public void processException(CommandSender sender, String given, Exception exception) {
        sender.sendMessage(ChatColor.RED + "'" + given + "' is not a valid boolean.");
    }
}
