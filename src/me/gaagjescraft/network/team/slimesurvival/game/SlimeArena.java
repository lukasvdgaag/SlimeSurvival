package me.gaagjescraft.network.team.slimesurvival.game;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaMode;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class SlimeArena {

    private String name;
    private Loc slimeSpawn;
    private List<Loc> waitingSpawns;
    private Loc lobbySpawn;
    private Loc spectatorSpawn;
    private int timer;
    private int minPlayers;
    private List<SlimePlayer> gamePlayers;
    private ArenaMode mode;
    private ArenaMode defaultMode;
    private ArenaState state;
    private String displayName;
    private boolean enabled;
    private boolean enableModeVoting;
    private List<Loc> joinSigns;
    private boolean isEditing;
    private boolean forceStart;

    public SlimeArena(String name) {
        this.name = name;
        this.waitingSpawns = new ArrayList<>();
        this.gamePlayers = new ArrayList<>();
        this.joinSigns = new ArrayList<>();
        this.displayName = ChatColor.AQUA + name;
        this.state = ArenaState.DISABLED;
        this.timer = SlimeSurvival.getCfg().getWaitingLobbyTimer();
        this.isEditing = false;
        this.forceStart = false;

        loadArenaData();
        saveArenaData();
    }

    public static boolean createNewArena(String name) {
        SlimeSurvival.get().addArena(new SlimeArena(name));
        SlimeArena arena = SlimeSurvival.getArena(name);


        boolean worldCreated = arena.createWorld();
        if (worldCreated) {
            return arena.createArenaFile();
        }
        return worldCreated;
    }

    private boolean createArenaFile() {
        File directory = new File(SlimeSurvival.get().getDataFolder(), "maps");
        if (!directory.mkdir()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create the arena folder");
            return false;
        }
        File file = new File(directory, name + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to create the arena file for arena " + name);
                e.printStackTrace();
                return false;
            }
        }

        SlimeUtils.copyDefaults(file, "arenaFile.yml");
        return true;
    }

    public boolean createWorld() {
        if (Bukkit.getWorld(name) == null) {
            WorldCreator wc = new WorldCreator(name);
            wc.environment(World.Environment.NORMAL);
            wc.generateStructures(false);
            wc.generator(new org.bukkit.generator.ChunkGenerator() {
                public final ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.BiomeGrid chunkGererator) {
                    ChunkData chunkData = createChunkData(world);
                    for (int i = 0; i < 16; i++) {
                        for (int j = 0; j < 16; j++) {
                            chunkGererator.setBiome(i, j, org.bukkit.block.Biome.valueOf("VOID"));
                        }
                    }
                    return chunkData;
                }
            });
            World w = wc.createWorld();
            w.setDifficulty(Difficulty.PEACEFUL);
            w.setSpawnFlags(true, true);
            w.setPVP(false);
            w.setStorm(false);
            w.setThundering(false);
            w.setKeepSpawnInMemory(false);
            w.setAutoSave(false);
            w.setGameRuleValue("doMobSpawning", "false");
            w.setGameRuleValue("showDeathMessages", "false");
            w.setGameRuleValue("announceAdvancements", "false");
            w.setGameRuleValue("doDaylightCycle", "false");
            w.setGameRuleValue("doWeatherCycle", "false");
        }
        return Bukkit.getWorld(name) != null;
    }

    public void saveWorld() {
        for (Loc loc : waitingSpawns) {
            if (loc.getLocation().getBlock().getType() == Material.BEACON) {
                loc.getLocation().getBlock().setType(Material.AIR);
            }
        }

        World w = Bukkit.getWorld(name);
        for (Entity e : w.getEntitiesByClass(Item.class)) {
            e.remove();
        }
        Bukkit.getWorld(name).save();
    }

    private void loadArenaData() {
        File mapFile = new File(SlimeSurvival.get().getDataFolder() + File.separator + "maps", name + ".yml");
        if (!mapFile.exists()) return;

        SlimeUtils.copyDefaults(mapFile, "arenaFile.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(mapFile);

        this.displayName = conf.getString("displayName", name);
        this.slimeSpawn = Loc.fromString(conf.getString("slimeSpawn"));
        this.lobbySpawn = Loc.fromString(conf.getString("lobbySpawn"));
        this.spectatorSpawn = Loc.fromString(conf.getString("spectatorSpawn"));
        this.minPlayers = conf.getInt("minPlayers");
        this.enabled = conf.getBoolean("enabled");
        this.enableModeVoting = conf.getBoolean("enableModeVoting");
        this.defaultMode = ArenaMode.valueOf(conf.getString("defaultMode"));
        if (enableModeVoting) this.mode = defaultMode;

        for (String sign : conf.getStringList("signs")) {
            Loc loc = Loc.fromString(sign);
            if (loc != null && loc.getLocation() != null) joinSigns.add(loc);
        }
        for (String spawn : conf.getStringList("waitingSpawns")) {
            Loc loc = Loc.fromString(spawn);
            if (loc != null && loc.getLocation() != null) waitingSpawns.add(loc);
        }

        int validStatus = SlimeUtils.isArenaValid(this);
        if (validStatus != 0) {
            enabled = false;
            state = ArenaState.DISABLED;
        }

    }

    public void prepareForEditing() {
        for (Loc loc : waitingSpawns) {
            loc.getLocation().getBlock().setType(Material.BEACON);
        }
    }

    public void saveArenaData() {
        File mapFile = new File(SlimeSurvival.get().getDataFolder() + File.separator + "maps", name + ".yml");
        if (!mapFile.exists()) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save arena data file. File '" + mapFile.getPath() + "' does not exist.");
            return;
        }

        SlimeUtils.copyDefaults(mapFile, "arenaFile.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(mapFile);

        conf.set("displayName", displayName);
        if (slimeSpawn != null) conf.set("slimeSpawn", slimeSpawn.toString());
        if (lobbySpawn != null) conf.set("lobbySpawn", lobbySpawn.toString());
        if (spectatorSpawn != null) conf.set("spectatorSpawn", spectatorSpawn.toString());
        conf.set("minPlayers", minPlayers);
        conf.set("enabled", enabled);
        conf.set("enableModeVoting", enableModeVoting);
        conf.set("defaultMode", defaultMode.name());

        List<String> signs = new ArrayList<>();
        for (Loc loc : joinSigns) {
            signs.add(loc.toString());
        }
        conf.set("signs", signs);

        List<String> spawns = new ArrayList<>();
        for (Loc loc : waitingSpawns) {
            spawns.add(loc.toString());
        }
        conf.set("waitingSpawns", spawns);

        try {
            conf.save(mapFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        for (SlimePlayer p : gamePlayers) {
            if (p != null) {
                leave(p);
            }
        }
        this.gamePlayers = new ArrayList<>();
        setState(ArenaState.RESTARTING);
    }

    public void leave(SlimePlayer player) {
        gamePlayers.remove(player);
        if (player.getPlayer() != null) {
            player.getPlayer().getPlayer().teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
        }
        player.repair();
    }

    public String getName() {
        return name;
    }

    public Loc getSlimeSpawn() {
        return slimeSpawn;
    }

    public void setSlimeSpawn(Loc slimeSpawn) {
        this.slimeSpawn = slimeSpawn;
    }

    public List<Loc> getWaitingSpawns() {
        return waitingSpawns;
    }

    public void addWaitingSpawn(Loc loc) {
        this.waitingSpawns.add(loc);
    }

    public void removeWaitingSpawn(Loc loc) {
        this.waitingSpawns.remove(loc);
    }

    public boolean hasWaitingSpawn(Loc loc) {
        for (Loc l : waitingSpawns) {
            if (l.matches(loc)) {
                return true;
            }
        }
        return false;
    }

    public Loc getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Loc lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
        saveArenaData();
    }

    public Loc getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setSpectatorSpawn(Loc spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
        saveArenaData();
    }

    public int getTimer() {
        return timer;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        saveArenaData();
    }

    public List<SlimePlayer> getAllPlayers() { return gamePlayers; }

    public List<SlimePlayer> getGamePlayers() {
        List<SlimePlayer> sps = Lists.newArrayList();
        for (SlimePlayer a : gamePlayers) {
            if (a.getTeam() != TeamType.SPECTATOR)
                sps.add(a);
        }
        return sps;
    }

    public List<SlimePlayer> getGamePlayers(TeamType teamType) {
        List<SlimePlayer> list = new ArrayList<>();
        for (SlimePlayer p : gamePlayers) {
            if (p.getTeam().equals(teamType)) {
                list.add(p);
            }
        }
        return list;
    }

    public ArenaMode getMode() {
        return mode;
    }

    public void setMode(ArenaMode mode) {
        this.mode = mode;
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        saveArenaData();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        saveArenaData();
    }

    public boolean isEnableModeVoting() {
        return enableModeVoting;
    }

    public void setEnableModeVoting(boolean enableModeVoting) {
        this.enableModeVoting = enableModeVoting;
        saveArenaData();
    }

    public List<Loc> getJoinSigns() {
        return joinSigns;
    }

    public void setDefaultMode(ArenaMode defaultMode) {
        this.defaultMode = defaultMode;
        saveArenaData();
    }

    public boolean isEditing() {
        return this.isEditing;
    }

    public void setEditing(boolean editing) {
        this.isEditing = editing;
    }

    public void start() {
        if (state == ArenaState.DISABLED || state == ArenaState.RESTARTING) {
            for (Player p : Bukkit.getWorld(name).getPlayers()) {
                p.teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
            }
            saveWorld();
            waitStart();
        }
    }

    private void startSlimeSelection() {
        List<SlimePlayer> sps = getGamePlayers();
        setState(ArenaState.STARTING);
        for (int i=0;i<sps.size();i++) {
            Player p = sps.get(i).getPlayer();
            p.closeInventory();
            p.getInventory().clear();

            // teleporting player to waiting spawn, then setting their look direction to slime spawn
            p.teleport(waitingSpawns.get(i).getLocation().add(0.5,0,0.5));
            Vector v = slimeSpawn.getLocation().clone().subtract(p.getEyeLocation()).toVector();
            Location l = p.getLocation().setDirection(v);
            p.teleport(l);

            p.setGameMode(GameMode.SURVIVAL);
            p.sendTitle("§a§lSlime Selection", "§7Starting the slime selection...", 20,60,20);

        }
    }

    private void waitStart() {
        setTimer(SlimeSurvival.getCfg().getWaitingLobbyTimer());
        setState(ArenaState.WAITING);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() != ArenaState.WAITING) cancel();

                if (getGamePlayers().size() >= getMinPlayers() || forceStart) {
                    // min player amount is reached, now starting countdown
                    if (getTimer() <= 0) {
                        cancel();
                        startSlimeSelection();
                    }
                    else {
                        if (getTimer() <= 5) {
                            for (SlimePlayer sp : getGamePlayers()) {
                                Player p = sp.getPlayer();
                                p.sendTitle("§a§l" + timer + " seconds",
                                        "§7before selecting slimes", 0, 20, 0);
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Starting slime selection in &2" + timer));
                                p.playSound(p.getLocation(),Sound.BLOCK_NOTE_PLING,1f,0.5f + (5-timer));
                            }
                        }
                    }
                    int timeLeft = getTimer() == 0 ? getTimer() : getTimer()-1;
                    setTimer(timeLeft);
                }
                else {
                    setTimer(SlimeSurvival.getCfg().getWaitingLobbyTimer());
                }
            }
        }.runTaskTimer(SlimeSurvival.get(),0L,20L);
    }

    public void broadcastMessage(String message) {
        for (SlimePlayer sp : getAllPlayers()) {
            sp.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public boolean addPlayer(Player player) {
        if (SlimeSurvival.getSlimePlayer(player) != null) {
            // player is in a game
            return false;
        }

        if (getState() == ArenaState.WAITING || getState() == ArenaState.STARTING) {
            if (gamePlayers.size() < waitingSpawns.size()) {
                SlimePlayer sp = new SlimePlayer(player, this);
                gamePlayers.add(sp);

                if (getState() == ArenaState.WAITING) {
                    // teleporting to waiting lobby

                    player.teleport(getLobbySpawn().getLocation());
                    sp.prepareInventoryForGame();
                }
                // todo add starting state teleportation

                return true;
            }
        }
        return false;
    }

    public void prepareForTeam(SlimePlayer player, TeamType type) {
        player.setTeam(type);
        Player p = player.getPlayer();
        p.getInventory().clear();

        if (type == TeamType.SLIME) {
            p.getInventory().setHelmet(ItemsManager.ARMOR_SLIME_HEAD);
            p.getInventory().setChestplate(ItemsManager.ARMOR_SLIME_CHESTPLATE);
            p.getInventory().setLeggings(ItemsManager.ARMOR_SLIME_LEGGINGS);
            p.getInventory().setBoots(ItemsManager.ARMOR_SLIME_BOOTS);

            p.getInventory().setItem(0, ItemsManager.ITEM_SLIME_THROWER);
            p.getInventory().setItem(1, ItemsManager.ITEM_SLIME_BOMBER);
        } else if (type == TeamType.SURVIVOR) {
            p.getInventory().setHelmet(ItemsManager.ARMOR_SURVIVOR_HELMET);
            p.getInventory().setChestplate(ItemsManager.ARMOR_SURVIVOR_CHESTPLATE);
            p.getInventory().setLeggings(ItemsManager.ARMOR_SURVIVOR_LEGGINGS);
            p.getInventory().setBoots(ItemsManager.ARMOR_SURVIVOR_BOOTS);
        }

    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public boolean isForceStart() {
        return forceStart;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }
}
