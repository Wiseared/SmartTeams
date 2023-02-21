package xyz.wiseared.smartdevelopment.teams.utils.command.adapters;

import xyz.wiseared.smartdevelopment.warsimulator.utils.command.schema.CachedCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ParameterAdapter<T> {

    /**
     * Called when it tries to get a object for a param
     *
     * @param input The input you receive like "hey"
     * @return T
     */

    T process(String input);

    /**
     * Called when there is a error like prasing error.
     *
     * @param sender    - Command Sender
     * @param given     - The input from proccess
     * @param exception - The error provided.
     */
    void processException(CommandSender sender, String given, Exception exception);

    // Will not be called anymore.
    @Deprecated
    //Label is not required idk what i was thinking because CachedCommand has it.
    default List<String> processTabComplete(CommandSender sender, String label, CachedCommand command) {
        return null;
    }

    /**
     * Called when a sender tab's after a argument
     *
     * @param sender  The sender that tabbed
     * @param command The command its calling from
     * @return The results from the tab complete, can be null.
     */

    default List<String> processTabComplete(CommandSender sender, CachedCommand command) {
        return null;
    }
}
