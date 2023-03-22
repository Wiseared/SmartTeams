package xyz.wiseared.smartdevelopment.teams.utils.command.adapters.defaults;

import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.ParameterAdapter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LongTypeAdapter implements ParameterAdapter<Long> {

    @Override
    public Long process(String str) {
        return Long.valueOf(str);
    }

    @Override
    public void processException(CommandSender sender, String given, Exception exception) {
        sender.sendMessage(ChatColor.RED + "'" + given + "' is not a valid number.");
    }
}
