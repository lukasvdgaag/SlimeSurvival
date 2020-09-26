package me.gaagjescraft.network.team.slimesurvival.utils;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
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

}
