package xyz.wiseared.smartdevelopment.teams.utils.command.tabcomplete.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import xyz.wiseared.smartdevelopment.teams.utils.command.Zetsu;
import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.ParameterAdapter;
import xyz.wiseared.smartdevelopment.teams.utils.command.processor.CommandProcessor;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.CachedCommand;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.CachedTabComplete;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.parameter.Completable;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.parameter.Default;
import xyz.wiseared.smartdevelopment.teams.utils.command.tabcomplete.TabCompleteHandler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Parameter;
import java.util.*;

@AllArgsConstructor
public class TabCompleteListener implements TabCompleter {

    //Instead of looping every time, we store the constant args ONCE
    private final Map<CachedCommand, Map<Integer, CachedTabComplete>> completionsCache =
            Maps.newLinkedHashMap();

    private final Zetsu zetsu;
    private final TabCompleteHandler handler;
    private final CommandProcessor processor;

    @Override

    public List<String> onTabComplete(CommandSender sender,
                                      Command ignored,
                                      String label,
                                      String[] args) {
        final CachedCommand command = processor.find(label.trim(), args);

        if (command == null) {
            return null; //should not happen but just in case
        }

        List<String> toReturn = handler.requestSubcommands(args.length != 0 ?
                label + Zetsu.CMD_SPLITTER + StringUtils.join(args, Zetsu.CMD_SPLITTER) : label);

        if (toReturn == null) {
            toReturn = Lists.newArrayList();
        }

        int start = args.length - command.getArgs().size();

        Map<Integer, CachedTabComplete> cache = completionsCache.computeIfAbsent(command, value -> {
            Map<Integer, CachedTabComplete> completions = Maps.newHashMap();

            int i = 1;
            for (Parameter parameter : value.getMethod().getParameters()) {
                if (parameter.getType() == CommandSender.class) continue;

                ParameterAdapter<?> parameterAdapter = zetsu.getParameterAdapters().get(parameter.getType());

                List<String> complete = new ArrayList<>();

                /* Values can change so we cannot do this. EX Bukkit.getOnlinePlayers();
                if (parameterAdapter != null && parameterAdapter.processTabComplete() != null) {
                    complete.addAll(parameterAdapter.processTabComplete());
                }*/

                if (parameter.isAnnotationPresent(Completable.class)) {
                    Completable completable = parameter.getAnnotation(Completable.class);

                    complete.addAll(Arrays.asList(completable.value()));
                }

                if (zetsu.isUseDefaultsInTabComplete() && parameter.isAnnotationPresent(Default.class)) {
                    complete.add(parameter.getAnnotation(Default.class).value());
                }

                completions.put(i++, new CachedTabComplete(parameterAdapter, complete));
            }

            return completions;
        });

        if (!cache.isEmpty() && cache.size() >= start) {
            CachedTabComplete complete = cache.get(start);

            if (complete != null) {
                toReturn.addAll(complete.getConstant());

                if (complete.getParameterAdapter() != null) {
                    List<String> tabComplete = complete.getParameterAdapter().processTabComplete(sender, command);

                    if (tabComplete != null) {
                        toReturn.addAll(tabComplete);
                    }
                }
            }
        }

        if (!toReturn.isEmpty()) {
            Collections.sort(toReturn);
        }

        return toReturn.isEmpty() ? null : toReturn;
    }
}
