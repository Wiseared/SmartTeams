package xyz.wiseared.smartdevelopment.teams.command.commands;

import lombok.AllArgsConstructor;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.command.CommandSender;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.utils.config.Config;
import xyz.wiseared.smartdevelopment.teams.utils.config.Menus;
import xyz.wiseared.smartdevelopment.teams.utils.config.Messages;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.Message;

import java.io.IOException;

@AllArgsConstructor
public class SmartTeamsCommand extends Message {

    private final SmartTeams plugin;

    @Command("smartteams")
    @Description("Main smartteams command")
    public final void smartTeamsCommand(@Sender CommandSender sender) {
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&5&lSmartTeams"));
        sender.sendMessage(CC.translate("&7- &dBy: &fWiseared"));
        sender.sendMessage(CC.translate("&7- &dVersion: &f" + plugin.getDescription().getVersion()));
        sender.sendMessage(CC.translate("&7- &dhttps://discord.smartdevelopment.tech"));
        sender.sendMessage(" ");
        sender.sendMessage(CC.CHAT_BAR);
    }

    @Command("smartteams reload")
    @Description("Reload all configs")
    @Permission("smartteams.reload")
    public final void smartTeamsReloadCommand(@Sender CommandSender sender) throws IOException {
        plugin.reloadConfig();
        plugin.saveConfig();
        plugin.getMenusYML().reloadConfig();
        plugin.getMessagesYML().reloadConfig();

        Config.init();
        Messages.init();
        Menus.init();

        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&7[&5&lSmartTeams&7] &aReloaded all configs"));
        sender.sendMessage(" ");
    }
}