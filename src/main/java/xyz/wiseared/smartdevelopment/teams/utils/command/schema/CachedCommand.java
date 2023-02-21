package xyz.wiseared.smartdevelopment.teams.utils.command.schema;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;
import xyz.wiseared.smartdevelopment.warsimulator.utils.command.Zetsu;
import xyz.wiseared.smartdevelopment.warsimulator.utils.command.schema.annotations.Command;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@ToString
@Getter
public class CachedCommand {

    private final String label;
    private final List<String> args;

    private final String description;
    private final boolean async;
    private final Method method;
    private final Object object;
    private final boolean playersOnly;

    public CachedCommand(String label,
                         List<String> args,
                         String description,
                         boolean async,
                         Method method,
                         Object object) {
        this.label = label;
        this.args = args;
        this.description = description;
        this.async = async;
        this.method = method;
        this.object = object;
        this.playersOnly = method.getParameters()[0].getType() == Player.class;
    }

    //TODO: Add default parameters

    public static List<CachedCommand> of(Command annotation, Method method, Object object) {
        final List<CachedCommand> commands = Lists.newArrayList();

        for (String label : annotation.labels()) {
            final String[] split = label.split(Zetsu.CMD_SPLITTER);

            commands.add(new CachedCommand(
                    split[0],
                    Arrays.asList(split).subList(1, split.length),
                    annotation.description(),
                    annotation.async(),
                    method,
                    object)
            );
        }

        return commands;
    }
}
