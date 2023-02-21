package xyz.wiseared.smartdevelopment.teams.utils.command.permissible.impl.permissible;

import xyz.wiseared.smartdevelopment.warsimulator.utils.command.permissible.PermissibleAttachment;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BukkitPermissionAttachment implements PermissibleAttachment<Permissible> {

    @Override
    public boolean test(Permissible annotation, CommandSender sender) {
        return sender.hasPermission(annotation.value());
    }

    @Override
    public void onFail(CommandSender sender, Permissible annotation) {
        sender.sendMessage(ChatColor.RED + "No Permission.");
    }
}
