package me.gaagjescraft.network.team.slimesurvival;

import me.gaagjescraft.network.team.slimesurvival.commands.arenas.ArenaCmdManager;
import me.gaagjescraft.network.team.slimesurvival.commands.general.GeneralCmdManager;
import me.gaagjescraft.network.team.slimesurvival.files.Config;
import me.gaagjescraft.network.team.slimesurvival.files.Messages;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.handlers.InGameHandler;
import me.gaagjescraft.network.team.slimesurvival.handlers.SignCreationHandler;
import me.gaagjescraft.network.team.slimesurvival.handlers.SpawnRemovalHandler;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlimeSurvival extends JavaPlugin {
    private static SlimeSurvival inst;
    private static ItemsManager itemsManager;
    private static Messages messages;
    private List<SlimeArena> arenas;
    private Config config;

    public static SlimeSurvival get() {
        return inst;
    }

    public static Messages getMessages() {
        return messages;
    }

    public static ItemsManager getIM() {
        return itemsManager;
    }

    public static SlimeArena getArena(String name) {
        for (SlimeArena a : get().getArenas()) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public static SlimePlayer getSlimePlayer(Player player) {
        for (SlimeArena a : get().getArenas()) {
            for (SlimePlayer sp : a.getGamePlayers()) {
                if (sp.getPlayer().equals(player)) {
                    return sp;
                }
            }
        }
        return null;
    }

    public static Config getCfg() {
        return get().config;
    }

    @Override
    public void onEnable() {
        inst = this;
        arenas = new ArrayList<>();

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        reloadConfig();

        config = new Config();
        messages = new Messages();
        itemsManager = new ItemsManager();

        loadArenas();

        registerCommandsAndEvents();
    }

    private void registerCommandsAndEvents() {
        getCommand("slimearena").setExecutor(new ArenaCmdManager());
        getCommand("slimesurvival").setExecutor(new GeneralCmdManager());

        Bukkit.getPluginManager().registerEvents(new SpawnRemovalHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InGameHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SignCreationHandler(), this);
    }

    @Override
    public void onDisable() {
        for (SlimeArena arena : getArenas()) {
            if (arena != null && arena.getAllPlayers() != null) {
                for (SlimePlayer player : arena.getAllPlayers()) {
                    arena.leave(player);
                }
            }
        }

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
                    SlimeArena arena = new SlimeArena(arenaFile.getName().replace(".yml", ""));
                    arenas.add(arena);

                    if (SlimeUtils.isArenaValid(arena) == 0 && arena.isEnabled()) {
                        arena.start();
                    }
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
