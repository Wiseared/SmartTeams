package xyz.wiseared.smartdevelopment.teams.utils.command.permissible;

import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;

/**
 * Basically conditions / permissions. Ran before the command is invoked
 * to test weather the command should be invoked or not.
 * <p>
 * ANNOTATION BASED
 *
 * @param <T>
 */
public interface PermissibleAttachment<T extends Annotation> {

    boolean test(T annotation, CommandSender sender);

    void onFail(CommandSender sender, T annotation);

}
