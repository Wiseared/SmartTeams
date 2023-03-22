package xyz.wiseared.smartdevelopment.teams.utils.command.processor;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import xyz.wiseared.smartdevelopment.teams.utils.command.Zetsu;
import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.ParameterAdapter;
import xyz.wiseared.smartdevelopment.teams.utils.command.permissible.PermissibleAttachment;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.CachedCommand;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CommandProcessor {

    protected final Zetsu zetsu;


    public CachedCommand find(String label, String[] args) {
        final List<CachedCommand> cmds = zetsu.getLabelMap().get(label); // idk never should be null.
        final List<String> newArgs = Lists.newArrayList(args);
        newArgs.removeIf(str -> str.trim().isEmpty());

        for (int i = newArgs.size(); i >= 0; i--) {
            String sentWithLabel = String.join(Zetsu.CMD_SPLITTER, newArgs.subList(0, i));

            for (CachedCommand cmd : cmds) {
                String cachedWithLabel = String.join(Zetsu.CMD_SPLITTER, cmd.getArgs());

                if (sentWithLabel.equalsIgnoreCase(cachedWithLabel)) {
                    return cmd;
                }
            }
        }

        return null;
    }

    //The args is for the parameter, IGNORE the subcommand type args
    protected void invoke(CachedCommand command, String[] args, CommandSender sender) {
        final Runnable runnable = () -> {
            final Method method = command.getMethod();
            int parameterCount = method.getParameterCount();
            final Object[] objects = new Object[parameterCount];

            if (method.getParameterCount() <= 0) {
                sender.sendMessage(ChatColor.RED + "This command is incorrectly setup!" +
                        " Please fix immediately. (Error: Invalid amount of method parameters)");
                return;
            }

            for (Class<? extends Annotation> aClass : zetsu.getPermissibleAttachments().keySet()
                    .stream()
                    .filter(method::isAnnotationPresent)
                    .collect(Collectors.toCollection(ArrayDeque::new))) { // ArrayDeque is faster than a HashSet
                final Annotation annotation = method.getAnnotation(aClass);
                final PermissibleAttachment<Annotation> attachment = getPermissibleAttachment(aClass);

                if (attachment != null && !attachment.test(annotation, sender)) {
                    attachment.onFail(sender, annotation);
                    return;
                }
            }

            if (command.isPlayersOnly() && !(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be ran by players.");
                return;
            }

            if ((parameterCount - 1) > args.length) {
                sendRequiredArgsMessage(sender, method, command.getArgs(), command.getLabel());
                return;
            }

            objects[0] = sender; //The first parameter is always the sender

            //If the parameter is a string and is the last
            // param in the method then we concat the arguments.
            StringBuilder strBuilder = new StringBuilder();

            for (int i = 0; i < args.length; i++) {
                if (parameterCount <= i + 1)
                    continue;

                final Parameter parameter = method.getParameters()[i + 1];

                if (!zetsu.getParameterAdapters().containsKey(parameter.getType())) {
                    sender.sendMessage(ChatColor.RED + "This command is incorrectly setup!" +
                            " Please fix immediately. (Error: Parameter Type does not have an adapter)");
                    return;
                }

                final ParameterAdapter<?> adapter = zetsu.getParameterAdapters().get(parameter.getType());

                if (parameter.getType() == String.class && parameterCount - 1 == i + 1) {
                    for (int j = i; j < args.length; j++) {
                        strBuilder.append(" ").append(args[j]);
                    }
                }

                try {
                    objects[i + 1] = strBuilder.length() != 0 ?
                            strBuilder.toString().trim() : adapter.process(args[i].trim());

                    if (objects[i + 1] == null) {
                        adapter.processException(sender, args[i], new NullPointerException());
                        return;
                    }
                } catch (Exception e) {
                    adapter.processException(sender, args[i], e);
                    return;
                }
            }

            try {
                method.invoke(command.getObject(), objects);
            } catch (IllegalAccessException | InvocationTargetException e) {
                sender.sendMessage(ChatColor.RED + "An error occurred while processing" +
                        " this command. Please contact a developer and report this as a bug.");
                e.printStackTrace();
            }
        };

        if (command.isAsync()) {
            Zetsu.EXECUTOR.execute(runnable);
            return;
        }

        runnable.run();
    }

    private void sendRequiredArgsMessage(CommandSender sender,
                                         Method method,
                                         List<String> args,
                                         String label) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED).append("Invalid Usage: /").append(label).append(" ");

        for (String arg : args) {
            builder.append(arg).append(" ");
        }

        for (int i = 1; i < method.getParameters().length; i++) {
            final Parameter parameter = method.getParameters()[i];

            if (parameter.isAnnotationPresent(Param.class)) {
                builder.append("<").append(parameter.getAnnotation(Param.class).value()).append("> ");
            } else {
                builder.append("<").append(parameter.getType().getSimpleName().toLowerCase()).append("> ");
            }
        }

        sender.sendMessage(builder.toString());
    }


    @SuppressWarnings("unchecked")
    private PermissibleAttachment<Annotation> getPermissibleAttachment(Class<? extends Annotation> clazz) {
        if (zetsu.getPermissibleAttachments().containsKey(clazz)) {
            return (PermissibleAttachment<Annotation>) zetsu.getPermissibleAttachments().get(clazz);
        }
        return null;
    }

}
