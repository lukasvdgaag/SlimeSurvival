package me.gaagjescraft.network.team.slimesurvival.utils;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class SlimeUtils {

    public static void copyDefaults(File yamlFileFromStorage, String fileResourceName) {
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(yamlFileFromStorage);
        Reader defConfigStream = new InputStreamReader(SlimeSurvival.get().getResource(fileResourceName));
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        playerConfig.options().copyDefaults(true);
        playerConfig.setDefaults(defConfig);
        try {
            playerConfig.save(yamlFileFromStorage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageList(Player a, List<String> b) {
        for (String c : b) {
            a.sendMessage(c(c));
        }
    }

    public static ItemStack getItemStackFromString(String a) {
        String[] b = a.split(":");
        ItemStack c;
        if (b.length <= 1) {
            c = new ItemStack(Material.getMaterial(a.toUpperCase()));
        }
        else {
            byte d = isInt(b[1]) ? Byte.parseByte(b[1]) : (byte) 0;
            c = new ItemStack(Material.getMaterial(b[0].toUpperCase()), 1, d);
        }
        return c;
    }

    public static void sendTitle(Player a, String b, int c, int d, int e) {
        String[] ab = b.split("\\\\n");
        String abc = "";
        String abcd = "";
        if (ab.length <= 1) {
            abc = c(b);
        }
        else {
            abc = c(ab[0]);
            abcd = c(ab[1]);
        }
        a.sendTitle(abc, abcd, c, d, e);
    }

    public static String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }
    public static List<String> c(List<String> a) {
        List<String> ab = Lists.newArrayList();
        for (String abc : a) {
            ab.add(c(abc));
        }
        return ab;
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int isArenaValid(SlimeArena arena) {
        // 0 = valid
        // 1 = slime spawn is not set
        // 2 = lobby spawn is not set
        // 3 = spectator spawn is not set
        // 4 = not enough waiting spawns
        // 5 = min players too low
        // 6 = arena is in edit mode
        if (arena.getSlimeSpawn() == null) {
            return 1;
        }
        else if (arena.getLobbySpawn() == null) {
            return 2;
        }
        else if (arena.getSpectatorSpawn() == null && SlimeSurvival.getCfg().isSpectateEnabled()) {
            return 3;
        }
        else if (arena.getWaitingSpawns().size() < 2) {
            return 4;
        }
        else if (arena.getMinPlayers() < 2) {
            return 5;
        }
        else if (arena.isEditing()) {
            return 6;
        }

        return 0;
    }

}
