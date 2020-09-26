package me.gaagjescraft.network.team.slimesurvival;

import me.gaagjescraft.network.team.slimesurvival.commands.arenas.ArenaCmdManager;
import me.gaagjescraft.network.team.slimesurvival.commands.general.GeneralCmdManager;
import me.gaagjescraft.network.team.slimesurvival.files.Config;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.handlers.SpawnRemovalHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlimeSurvival extends JavaPlugin {
    private static SlimeSurvival inst;
    private List<SlimeArena> arenas;
    private Config config;

    public static SlimeSurvival get() { return inst; }

    public static SlimeArena getArena(String name) {
        for (SlimeArena a : get().getArenas()) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public static Config getCfg() { return get().config; }

    @Override
    public void onEnable() {
        inst = this;
        arenas = new ArrayList<>();

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();

        config = new Config();

        loadArenas();

        registerCommandsAndEvents();
    }

    private void registerCommandsAndEvents() {
        getCommand("ssa").setExecutor(new ArenaCmdManager());
        getCommand("slimesurvival").setExecutor(new GeneralCmdManager());

        Bukkit.getPluginManager().registerEvents(new SpawnRemovalHandler(), this);
    }

    @Override
    public void onDisable() {

    }

    public void loadArenas() {
        for (SlimeArena a : arenas) {
            a.stop();
        }
        arenas.clear();

        File folder = new File(getDataFolder(), "maps");
        if (folder.exists() && folder.isDirectory() && folder.listFiles() != null) {
            for (File arenaFile : Objects.requireNonNull(folder.listFiles())) {
                if (arenaFile.getName().endsWith(".yml")) {
                    arenas.add(new SlimeArena(arenaFile.getName().replace(".yml", "")));
                }
            }
        }
    }

    public List<SlimeArena> getArenas() {
        return arenas;
    }

    public void addArena(SlimeArena arena) {
        this.arenas.add(arena);
    }

    public void removeArena(SlimeArena arena) {
        this.arenas.remove(arena);
    }
}
