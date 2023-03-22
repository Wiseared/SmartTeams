package xyz.wiseared.smartdevelopment.teams.utils.command.processor.bukkit;

import xyz.wiseared.smartdevelopment.teams.utils.command.processor.impl.SpigotProcessor;
import xyz.wiseared.smartdevelopment.teams.utils.command.tabcomplete.TabCompleteHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {

    private final SpigotProcessor processor;
    private final TabCompleteHandler handler;

    private final String fallbackPrefix;

    public BukkitCommand(String name, SpigotProcessor processor, TabCompleteHandler handler, String fallbackPrefix) {
        super(name);
        this.processor = processor;
        this.handler = handler;
        this.fallbackPrefix = fallbackPrefix;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (s.startsWith(fallbackPrefix)) {
            s = s.replace(fallbackPrefix + ":", "");
        }

        return processor.onCommand(commandSender, this, s, strings);
    }
}
