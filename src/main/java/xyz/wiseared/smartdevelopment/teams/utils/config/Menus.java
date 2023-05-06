package xyz.wiseared.smartdevelopment.teams.utils.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.wiseared.smartdevelopment.teams.SmartTeams;
import xyz.wiseared.smartdevelopment.teams.utils.others.CC;
import xyz.wiseared.smartdevelopment.teams.utils.others.ItemBuilder;

public class Menus {

    public static String TEAM_MANAGE_TITLE;
    public static int TEAM_MANAGE_SIZE;

    public static int TEAM_MANAGE_SET_HOME_SLOT;
    public static ItemStack TEAM_MANAGE_SET_HOME;

    public static int TEAM_MANAGE_MEMBER_MANAGE_SLOT;
    public static ItemStack TEAM_MANAGE_MEMBER_MANAGE;

    public static int TEAM_MANAGE_DISBAND_SLOT;
    public static ItemStack TEAM_MANAGE_DISBAND;

    public static void init() {
        FileConfiguration config = SmartTeams.getInstance().getMessagesYML().getConfig();

        TEAM_MANAGE_TITLE = config.getString("TEAM_MANAGE_MENU.TITLE");
        TEAM_MANAGE_SIZE = config.getInt("TEAM_MANAGE_MENU.SIZE");

        TEAM_MANAGE_SET_HOME_SLOT = config.getInt("TEAM_MANAGE_MENU.ITEMS.SET_HOME.SLOT");
        TEAM_MANAGE_MEMBER_MANAGE_SLOT = config.getInt("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.SLOT");
        TEAM_MANAGE_DISBAND_SLOT = config.getInt("TEAM_MANAGE_MENU.ITEMS.DISBAND.SLOT");

        TEAM_MANAGE_SET_HOME = new ItemBuilder(Material.valueOf(config.getString("TEAM_MANAGE_MENU.ITEMS.SET_HOME.MATERIAL")))
                .name(CC.translate(config.getString("TEAM_MANAGE_MENU.ITEMS.SET_HOME.NAME")))
                .data((short) config.getInt("TEAM_MANAGE_MENU.ITEMS.SET_HOME.DATA"))
                .amount(config.getInt("TEAM_MANAGE_MENU.ITEMS.SET_HOME.AMOUNT"))
                .lore(CC.translate(config.getStringList("TEAM_MANAGE_MENU.ITEMS.SET_HOME.LORE")))
                .build();

        TEAM_MANAGE_MEMBER_MANAGE = new ItemBuilder(Material.valueOf(config.getString("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.MATERIAL")))
                .name(CC.translate(config.getString("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.NAME")))
                .data((short) config.getInt("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.DATA"))
                .amount(config.getInt("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.AMOUNT"))
                .lore(CC.translate(config.getStringList("TEAM_MANAGE_MENU.ITEMS.MEMBER_MANAGE.LORE")))
                .build();

        TEAM_MANAGE_DISBAND = new ItemBuilder(Material.valueOf(config.getString("TEAM_MANAGE_MENU.ITEMS.DISBAND.MATERIAL")))
                .name(CC.translate(config.getString("TEAM_MANAGE_MENU.ITEMS.DISBAND.NAME")))
                .data((short) config.getInt("TEAM_MANAGE_MENU.ITEMS.DISBAND.DATA"))
                .amount(config.getInt("TEAM_MANAGE_MENU.ITEMS.DISBAND.AMOUNT"))
                .lore(CC.translate(config.getStringList("TEAM_MANAGE_MENU.ITEMS.DISBAND.LORE")))
                .build();
    }
}