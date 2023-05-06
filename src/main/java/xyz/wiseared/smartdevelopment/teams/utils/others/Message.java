package xyz.wiseared.smartdevelopment.teams.utils.others;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.wiseared.smartdevelopment.teams.utils.config.Messages;

import java.util.List;
public abstract class Message {

    public void sendMessage(Player player, String s) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            player.sendMessage(CC.translate(PlaceholderAPI.setPlaceholders(player, s)));
        } else {
            player.sendMessage(CC.translate(s));
        }
    }

    public void sendMessage(CommandSender sender, String s) {
        sender.sendMessage(CC.translate(s));
    }

    public void sendMessage(Player player, List<String> s) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            for (String msg : s) {
                player.sendMessage(CC.translate(PlaceholderAPI.setPlaceholders(player, msg)));
            }
        } else {
            for (String msg : s) {
                player.sendMessage(CC.translate(msg));
            }
        }
    }

    public void sendMessage(CommandSender sender, List<String> s) {
        for (String msg : s) {
            sender.sendMessage(CC.translate(msg));
        }
    }

    public void sendOnlyPlayer(CommandSender sender) {
        Messages.ONLY_PLAYER.forEach(s -> sender.sendMessage(CC.translate(s)));
    }
}