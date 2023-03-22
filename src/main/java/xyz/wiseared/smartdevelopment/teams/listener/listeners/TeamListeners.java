package xyz.wiseared.smartdevelopment.teams.listener.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.user.User;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;

public class TeamListeners implements Listener {

    private final SmartTeams plugin;

    public TeamListeners(SmartTeams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        User damager = plugin.getUserManager().getUsers().get(event.getDamager().getUniqueId());
        User player = plugin.getUserManager().getUsers().get(event.getEntity().getUniqueId());

        if (damager.getTeam() == player.getTeam()) {
            event.setCancelled(true);
            event.getDamager().sendMessage(CC.translate(plugin.getMessagesYML().getConfig().getString("TEAMS.CANT_DAMAGE_TEAM")));
        }
    }
}