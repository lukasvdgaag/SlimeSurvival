package me.gaagjescraft.network.team.slimesurvival.utils;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

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

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
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