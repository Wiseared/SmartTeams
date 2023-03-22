package xyz.wiseared.smartdevelopment.teams.utils.command;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.ParameterAdapter;
import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.*;
import xyz.wiseared.smartdevelopment.teams.utils.command.adapters.defaults.*;
import xyz.wiseared.smartdevelopment.teams.utils.command.permissible.PermissibleAttachment;
import xyz.wiseared.smartdevelopment.teams.utils.command.permissible.impl.permissible.BukkitPermissionAttachment;
import xyz.wiseared.smartdevelopment.teams.utils.command.permissible.impl.permissible.Permissible;
import xyz.wiseared.smartdevelopment.teams.utils.command.processor.bukkit.BukkitCommand;
import xyz.wiseared.smartdevelopment.teams.utils.command.processor.impl.SpigotProcessor;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.CachedCommand;
import xyz.wiseared.smartdevelopment.teams.utils.command.schema.annotations.Command;
import xyz.wiseared.smartdevelopment.teams.utils.command.tabcomplete.TabCompleteHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class Zetsu {

    // Dedicated executor for Zetsu
    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Zetsu Thread - %1$d")
            .build());

    public static String CMD_SPLITTER = " "; //Splitter for commands / arguments

    // Storing labels & commands associated with the label is faster
    // than looping through all of the labels for no reason.
    private final Map<String, List<CachedCommand>> labelMap = Maps.newHashMap();
    private final Map<Class<?>, ParameterAdapter<?>> parameterAdapters = Maps.newConcurrentMap(); //Multithreading :D
    private final Map<Class<? extends Annotation>, PermissibleAttachment<? extends Annotation>> permissibleAttachments
            = Maps.newConcurrentMap();
    private final SpigotProcessor processor = new SpigotProcessor(this);
    private final TabCompleteHandler tabCompleteHandler = new TabCompleteHandler(this);
    private final JavaPlugin plugin;
    private CommandMap commandMap = getCommandMap();

    @Setter
    private String fallbackPrefix = "commands";

    @Setter
    @Getter
    private boolean useDefaultsInTabComplete = false;

    public Zetsu(JavaPlugin plugin) {
        this.plugin = plugin;

        registerParameterAdapter(String.class, new StringTypeAdapter());
        registerParameterAdapter(Player.class, new PlayerTypeAdapter());
        registerParameterAdapter(Integer.class, new IntegerTypeAdapter());
        registerParameterAdapter(Double.class, new DoubleTypeAdapter());
        registerParameterAdapter(Boolean.class, new BooleanTypeAdapter());
        registerParameterAdapter(Long.class, new LongTypeAdapter());

        registerPermissibleAttachment(Permissible.class, new BukkitPermissionAttachment());
    }

    /**
     * Register multiple commands using object instances.
     */
    public void registerCommands(Object... objects) {
        for (Object object : objects) {
            registerCommand(object);
        }
    }

    /**
     * Register a ParameterAdapter
     *
     * @param clazz   The class for the adapter
     * @param adapter The adapter
     * @param <T>     Type
     */

    public <T> void registerParameterAdapter(Class<T> clazz, ParameterAdapter<T> adapter) {
        parameterAdapters.putIfAbsent(clazz, adapter);
    }

    /**
     * Register a PermissibleAttachment
     *
     * @param clazz      The class for the attachment
     * @param attachment The attachment
     * @param <T>        Type
     */
    public <T extends Annotation> void registerPermissibleAttachment(Class<T> clazz,
                                                                     PermissibleAttachment<T> attachment) {
        permissibleAttachments.putIfAbsent(clazz, attachment);
    }

    /**
     * Register a command using a object instance.
     */
    private void registerCommand(Object object) {
        if (object == null) {
            return;
        }

        if (commandMap == null) {
            commandMap = getCommandMap();
        }

        for (Method method : object.getClass().getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) {
                continue;
            }

            List<CachedCommand> commands = CachedCommand.of(method.getAnnotation(Command.class), method, object);

            for (CachedCommand command : commands) {
                if (commandMap != null) {
                    org.bukkit.command.Command cmd = commandMap.getCommand(command.getLabel());

                    if (cmd == null) {
                        BukkitCommand bukkitCommand = new BukkitCommand(
                                command.getLabel(),
                                processor,
                                tabCompleteHandler,
                                fallbackPrefix);
                        bukkitCommand.setDescription(command.getDescription());

                        commandMap.register(fallbackPrefix, bukkitCommand);
                    }

                    labelMap.putIfAbsent(command.getLabel(), new ArrayList<>());
                    labelMap.get(command.getLabel()).add(command);
                    labelMap.get(command.getLabel()).sort((o1, o2) ->
                            o2.getMethod().getParameterCount() - o1.getMethod().getParameterCount());
                }
            }
        }
    }

    /**
     * Removes a command and unregisters it. WIP
     *
     * @param label Command Top Level Name so "/command test test1" would be "command"
     */
    //TODO: Make it object based.
    private boolean removeCommand(String label) {
        if (commandMap != null) {
            labelMap.remove(label);
            return commandMap.getCommand(label).unregister(commandMap);
        }

        return false;
    }


    private CommandMap getCommandMap() {
        final PluginManager manager = Bukkit.getPluginManager();

        try {
            Field field = manager.getClass().getDeclaredField("commandMap");

            field.setAccessible(true);

            return (CommandMap) field.get(manager);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

}
