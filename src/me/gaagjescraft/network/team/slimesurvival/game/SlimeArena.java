package me.gaagjescraft.network.team.slimesurvival.game;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaMode;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.managers.ArenaWaitingCageManager;
import me.gaagjescraft.network.team.slimesurvival.managers.SignManager;
import me.gaagjescraft.network.team.slimesurvival.managers.SlimeThrowerManager;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.menus.VoteModeMenu;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
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
    private SlimeThrowerManager slimeThrowerManager;
    private ArenaWaitingCageManager arenaWaitingCageManager;
    private VoteModeMenu voteModeMenu;
    private SignManager arenaSignManager;
    private int calculatedSlimeTeamPlayers;
    private boolean slimesReleased;
    private List<SlimePlayer> winners;
    private TeamType winningTeam;
    private boolean hasAwarderWinners;
    private int debugId = 1;

    public SlimeArena(String name) {
        this.name = name;
        this.waitingSpawns = new ArrayList<>();
        this.gamePlayers = new ArrayList<>();
        this.joinSigns = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.displayName = ChatColor.AQUA + name;
        this.state = ArenaState.DISABLED;
        this.mode = ArenaMode.NORMAL;
        this.timer = SlimeSurvival.getCfg().getWaitingLobbyTimer();
        this.slimeThrowerManager = new SlimeThrowerManager(this);
        this.arenaWaitingCageManager = new ArenaWaitingCageManager(this);
        this.arenaSignManager = new SignManager(this);
        this.slimesReleased = false;
        this.winningTeam = null;
        this.hasAwarderWinners = false;
        this.voteModeMenu = new VoteModeMenu(this);

        createArenaFile();
        createWorld();
        loadArenaData();
        saveArenaData();
        getSignManager().updateAll();
    }

    public VoteModeMenu getVoteModeMenu() {
        return voteModeMenu;
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

    private void resetArenaData() {
        this.waitingSpawns = new ArrayList<>();
        this.gamePlayers = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.displayName = ChatColor.AQUA + name;
        this.state = ArenaState.DISABLED;
        this.mode = ArenaMode.NORMAL;
        this.timer = SlimeSurvival.getCfg().getWaitingLobbyTimer();
        this.calculatedSlimeTeamPlayers = 0;
        this.slimesReleased = false;
        this.winningTeam = null;
        this.hasAwarderWinners = false;
        debugId++;
        for (Loc l : waitingSpawns) {
            getWaitingCageManager().removeCage(l);
        }
    }

    public void calculateSlimeTeamPlayers() {
        if (this.mode == ArenaMode.FREEZE) {
            this.calculatedSlimeTeamPlayers = (int) (getGamePlayers().size() / 3);
        } else {
            this.calculatedSlimeTeamPlayers = 1;
        }
    }

    private boolean createArenaFile() {
        File directory = new File(SlimeSurvival.get().getDataFolder(), "maps");
        if (!directory.exists() && !directory.mkdirs()) {
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
            wc.createWorld();
        }
        World w =Bukkit.getWorld(name);
        w.setDifficulty(Difficulty.EASY);
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
        return true;
    }

    public void saveWorld() {
        for (Loc loc : waitingSpawns) {
            if (loc.getLocation().getBlock().getType() == Material.BEACON) {
                loc.getLocation().getBlock().setType(Material.AIR);
            }
        }

        removeEntities();
        Bukkit.getWorld(name).save();
    }

    private void removeEntities() {
        World w = Bukkit.getWorld(name);
        for (Entity e : w.getEntitiesByClass(ArmorStand.class)) {
            if (e.hasMetadata("slimesurvival")) {
                e.remove();
            }
        }
        for (Entity e : w.getEntitiesByClass(Item.class)) {
            e.remove();
        }
        for (Entity e : w.getLivingEntities()) {
            if (e.getType() != EntityType.PLAYER) {
                e.remove();
            }
        }
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
        this.forceStart = false;
        this.isEditing = false;
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
        Bukkit.getWorld(name).setAutoSave(false);
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
        if (getState() == ArenaState.RESTARTING) return;

        setState(ArenaState.RESTARTING);
        List<SlimePlayer> gps = Lists.newArrayList(getAllPlayers());
        for (SlimePlayer p : gps) {
            if (p != null) {
                leave(p);
            }
        }
        this.gamePlayers = new ArrayList<>();
        saveWorld();

        if (enabled) {
            resetArenaData();
            loadArenaData();
            Bukkit.getScheduler().runTaskLater(SlimeSurvival.get(),this::waitStart,40);
        } else {
            setState(ArenaState.DISABLED);
        }
    }

    public void endMatch() {
        if (getState() == ArenaState.ENDING) return;

        setState(ArenaState.ENDING);
        setTimer(SlimeSurvival.getCfg().getEndTimer());
        removeEntities();

        checkForWin();
        awardWinners();

        // todo add slime exploding
        for (SlimePlayer sp : getGamePlayers()) {
            sp.getPlayer().getInventory().clear();
            //SlimeSurvival.getMessages().getGameEnded().addVar("%time%", getTimer()+"").send(sp.getPlayer());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getTimer() <= 0 || gamePlayers.size() == 0) {
                    stop();
                    this.cancel();
                    return;
                }

                setTimer(getTimer() - 1);
            }
        }.runTaskTimer(SlimeSurvival.get(), 0, 20);

    }

    public void leave(SlimePlayer player) {
        if (state == ArenaState.STARTING) {
            getWaitingCageManager().removeCage(player.getWaitingSpawn());
        }
        gamePlayers.remove(player);

        if (state == ArenaState.WAITING || state == ArenaState.STARTING) {
            for (SlimePlayer sps : getGamePlayers()) {
                SlimeSurvival.getMessages().getLeaveGame()
                        .addVar("%player%", player.getPlayer().getName())
                        .addVar("%players%", getGamePlayers().size()+"")
                        .addVar("%maxplayers%", getWaitingSpawns().size()+"")
                        .send(sps.getPlayer());
            }
        }

        if (state == ArenaState.PLAYING && getGamePlayers().size() > 1) {
            if (player.getTeam() == TeamType.SLIME && player.isMainSlime()) {
                boolean changed = false;
                for (SlimePlayer sp : getGamePlayers(TeamType.SLIME)) {
                    sp.setMainSlime(true);
                    changed = true;
                    // todo make configurable
                    for (SlimePlayer sp1 : getGamePlayers()) {
                        sp1.getPlayer().sendMessage("§2Main slime left. " + sp1.getPlayer().getName() + " is now the main slime!");
                    }
                    break;
                }

                if (!changed) {
                    for (SlimePlayer sp : getGamePlayers(TeamType.SURVIVOR)) {
                        sp.getPlayer().sendMessage("§2Main slime left. " + sp.getPlayer().getName() + " is now the main slime!");
                        prepareForTeam(sp, TeamType.SLIME);
                        sp.setMainSlime(true);
                        break;
                    }
                }
            }
        }


        getSignManager().updateAll();
        if (player.getPlayer() != null) {
            player.getPlayer().getPlayer().teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
        }
        player.repair();

        if (gamePlayers.size() == 0) {
            stop();
        } else if (gamePlayers.size() == 1) {
            endMatch();
        }
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

    public void addSign(Loc loc){
        this.joinSigns.add(loc);
    }

    public void removeSign(Loc loc) {
        this.joinSigns.remove(loc);
    }

    public boolean hasSign(Loc loc) {
        for (Loc l : joinSigns) {
            if (l.matches(loc)) {
                return true;
            }
        }
        return false;
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

    public void setTimer(int timer) {
        this.timer = timer;

        for (SlimePlayer sp : getAllPlayers()) {
            if (getState() == ArenaState.PLAYING && timer <= SlimeSurvival.getCfg().getGameTimer() - SlimeSurvival.getCfg().getLeadTimer()) {
                if (getMode() != ArenaMode.FREEZE || !sp.isCompromised()) {
                    sp.getPlayer().setExp(0);
                }
                sp.getPlayer().setLevel(timer);
            }
            else if (getState() == ArenaState.ENDING) {
                sp.getPlayer().setExp((float) (timer) / SlimeSurvival.getCfg().getEndTimer());
                sp.getPlayer().setLevel(timer);
            }
            else if (getState() == ArenaState.WAITING) {
                sp.getPlayer().setExp((float) (timer) / SlimeSurvival.getCfg().getWaitingLobbyTimer());
                sp.getPlayer().setLevel(timer);
            }
            sp.updateScoreboard();
        }
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        saveArenaData();
    }

    public List<SlimePlayer> getAllPlayers() {
        return gamePlayers;
    }

    public List<SlimePlayer> getNonSlimeGamePlayers() {
        List<SlimePlayer> sps = Lists.newArrayList();
        for (SlimePlayer a : gamePlayers) {
            if (a.getTeam() != TeamType.SPECTATOR && a.getTeam() != TeamType.SLIME)
                sps.add(a);
        }
        return sps;
    }

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
        if (gamePlayers == null) return list;
        for (SlimePlayer p : gamePlayers) {
            if ((teamType == p.getTeam())) {
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
        getSignManager().updateAll();
        for (SlimePlayer sp : getGamePlayers()) {
            sp.updateScoreboard();
        }
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
        if (state == ArenaState.DISABLED) {
            for (Player p : Bukkit.getWorld(name).getPlayers()) {
                p.teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
            }
            saveWorld();
            waitStart();
        }
    }

    private void determineMode() {
        if (enableModeVoting) {
            int normalVotes = getModeVotes(ArenaMode.NORMAL);
            int freezeVotes = getModeVotes(ArenaMode.FREEZE);
            int classicVotes = getModeVotes(ArenaMode.CLASSIC);
            if (normalVotes == 0 && freezeVotes == 0 && classicVotes == 0) {
                this.mode = ArenaMode.NORMAL;
            }
            else if (classicVotes > freezeVotes && classicVotes > normalVotes) {
                this.mode = ArenaMode.CLASSIC;
            }
            else if (freezeVotes > normalVotes && freezeVotes > classicVotes) {
                this.mode = ArenaMode.FREEZE;
            }
            else {
                this.mode = ArenaMode.NORMAL;
            }
        }
        else {
            this.mode = defaultMode;
        }
    }

    public void startMatch() {
        getSlimeThrowerManager().removeSelectorSlime();

        setState(ArenaState.PLAYING);
        setTimer(SlimeSurvival.getCfg().getGameTimer());

        // making all non-slime players survivors
        for (SlimePlayer sp : getGamePlayers()) {
            if (sp.getTeam() != TeamType.SLIME) {
                prepareForTeam(sp, TeamType.SURVIVOR);
                getWaitingCageManager().removeCage(sp.getWaitingSpawn());
                sp.getPlayer().setWalkSpeed(0.3f);
                SlimeSurvival.getMessages().getReleaseSurvivorsTitle().sendTitle(sp.getPlayer(),10,40,10);
            } else {
                sp.getPlayer().setWalkSpeed(0.25f);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getTimer() <= 0 || getState() != ArenaState.PLAYING) {
                    this.cancel();
                    endMatch();
                    return;
                }
                if (getTimer() == SlimeSurvival.getCfg().getGameTimer() - SlimeSurvival.getCfg().getLeadTimer()) {
                    // lead time for survivors is over, releasing the slimes...
                    for (SlimePlayer sp : getGamePlayers(TeamType.SLIME)) {
                        getWaitingCageManager().removeCage(sp.getWaitingSpawn());
                        SlimeSurvival.getMessages().getReleaseSlimesTitle().sendTitle(sp.getPlayer(), 10, 40, 10);
                        SlimeSurvival.getMessages().getReleaseSlimesForSurvivors().send(sp.getPlayer());
                    }
                    for (SlimePlayer sp : getGamePlayers(TeamType.SURVIVOR)) {
                        SlimeSurvival.getMessages().getReleaseSlimesForSurvivors().send(sp.getPlayer());
                        SlimeSurvival.getMessages().getReleaseSlimesForSurvivorsTitle().sendTitle(sp.getPlayer(),10,40,10);
                    }
                    setSlimesReleased(true);
                }

                setTimer(getTimer() - 1);
            }
        }.runTaskTimer(SlimeSurvival.get(), 0, 20);

    }

    private void startSlimeSelection() {
        List<SlimePlayer> sps = getGamePlayers();
        setState(ArenaState.STARTING);
        determineMode();
        for (int i = 0; i < sps.size(); i++) {
            SlimePlayer sp = sps.get(i);
            Player p = sp.getPlayer();
            p.closeInventory();
            p.getInventory().clear();

            // teleporting player to waiting spawn, then setting their look direction to slime spawn
            Loc waitingSpawn = waitingSpawns.get(i);
            getWaitingCageManager().spawnCage(waitingSpawn);

            p.teleport(waitingSpawn.getLocation().add(0.5, 0, 0.5));
            sp.setAssignedWaitingSpawn(waitingSpawn);
            Vector v = slimeSpawn.getLocation().clone().subtract(p.getEyeLocation()).toVector();
            Location l = p.getLocation().setDirection(v);
            p.teleport(l);

            p.setGameMode(GameMode.SURVIVAL);
            SlimeSurvival.getMessages().getStartSlimeSelectionTitle().sendTitle(p,20,60,20);

            if (mode == ArenaMode.FREEZE) {
                SlimeSurvival.getMessages().getFreezeGameDescription().send(p);
            } else if (mode == ArenaMode.NORMAL) {
                SlimeSurvival.getMessages().getNormalGameDescription().send(p);
            } else if (mode == ArenaMode.CLASSIC) {
                SlimeSurvival.getMessages().getClassicGameDescription().send(p);
            }
        }

        setTimer(10);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() != ArenaState.STARTING) {
                    this.cancel();
                    return;
                }

                if (getTimer() <= 0) {
                    calculateSlimeTeamPlayers();
                    for (SlimePlayer sp : getGamePlayers()) {
                        SlimeSurvival.getMessages().getSelectingSlimeAmountMessage().addVar("%amount%", getCalculatedSlimeTeamPlayers()+"").sendTitle(sp.getPlayer(),5,30,5);
                        SlimeSurvival.getMessages().getSelectingSlimeAmountTitle().addVar("%amount%", getCalculatedSlimeTeamPlayers()+"").send(sp.getPlayer());
                    }

                    SlimePlayer randomPlayer = sps.get(new Random().nextInt(sps.size()));
                    getSlimeThrowerManager().spawnSelectorSlime(randomPlayer);
                    this.cancel();
                    return;
                }
                if (getTimer() <= 5 && getTimer() > 0) {
                    for (SlimePlayer sp : getGamePlayers()) {
                        SlimeSurvival.getMessages().getStartSlimeSelectionCountdownTitle().addVar("%time%",getTimer()+"").sendTitle(sp.getPlayer(),5,10,5);
                    }
                }
                setTimer(getTimer() - 1);
            }
        }.runTaskTimer(SlimeSurvival.get(), 0, 20);

    }

    private void waitStart() {
        if (getState() == ArenaState.WAITING) return;

        setTimer(SlimeSurvival.getCfg().getWaitingLobbyTimer());
        setState(ArenaState.WAITING);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() != ArenaState.WAITING) this.cancel();

                if (!getGamePlayers().isEmpty() && (getGamePlayers().size() >= getMinPlayers() || isForceStart())) {
                    // min player amount is reached, now starting countdown
                    if (getTimer() <= 0) {
                        this.cancel();
                        startSlimeSelection();
                        return;
                    } else {
                        if (getTimer() == 10 || getTimer() == 30) {
                            for (SlimePlayer sp : getGamePlayers()) {
                                SlimeSurvival.getMessages().getCountdownSlimeSelectionTeleportation().addVar("%time%", getTimer()+"").send(sp.getPlayer());
                                SlimeSurvival.getMessages().getCountdownSlimeSelectionTeleportationTitle().addVar("%time%", getTimer()+"").sendTitle(sp.getPlayer(),5,10,5);
                            }
                        }
                        if (getTimer() <= 5) {
                            for (SlimePlayer sp : getGamePlayers()) {
                                SlimeSurvival.getMessages().getCountdownSlimeSelectionTeleportationTitle().addVar("%time%", getTimer()+"").sendTitle(sp.getPlayer(),5,10,5);
                                SlimeSurvival.getMessages().getCountdownSlimeSelectionTeleportation().addVar("%time%", getTimer()+"").send(sp.getPlayer());
                                sp.getPlayer().playSound(sp.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 0.5f + (5 - timer));
                            }
                        }
                    }
                    setTimer(getTimer()-1);
                } else {
                    setTimer(SlimeSurvival.getCfg().getWaitingLobbyTimer());
                }
            }
        }.runTaskTimer(SlimeSurvival.get(), 0L, 20L);
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
                    player.teleport(getLobbySpawn().getLocation());
                    sp.prepareInventoryForGame();
                } else {
                    for (Loc loc : getWaitingSpawns()) {
                        if (getPlayerFromWaitingSpawn(loc) == null) {
                            sp.setAssignedWaitingSpawn(loc);
                            player.teleport(loc.getLocation().add(0.5,0,0.5));
                            getWaitingCageManager().spawnCage(loc);
                            sp.prepareInventoryForGame();
                            break;
                        }
                    }
                }

                for (SlimePlayer sps : getGamePlayers()) {
                    SlimeSurvival.getMessages().getJoinGame()
                            .addVar("%player%", sp.getPlayer().getName())
                            .addVar("%players%", getGamePlayers().size()+"")
                            .addVar("%maxplayers%", getWaitingSpawns().size()+"")
                            .send(sps.getPlayer());
                }

                getSignManager().updateAll();
                return true;
            }
        }
        return false;
    }

    public void awardWinners() {
        // todo make these message configurable
        // todo add win commands (maybe additionsplus integration)
        // todo add player stats
        if (hasAwarderWinners) return;

        for (SlimePlayer loser : getGamePlayers()) {
            if (winningTeam == TeamType.SURVIVOR) {
                loser.getPlayer().sendTitle("§7§lSURVIVORS WON!", "", 10, 60, 10);
                loser.getPlayer().sendMessage("§7Survivors have won the game!");
            }
            else if (winningTeam == TeamType.SLIME){
                loser.getPlayer().sendTitle("§2§lSLIMES WON!", "", 10, 60, 10);
                loser.getPlayer().sendMessage("§2Slimes have won the game!");
            }

            if (!winners.contains(loser)) {
                // loser is an actual loser
                // todo add loss here
            }

        }
        hasAwarderWinners = true;
    }

    public void checkForWin() {
        winners = new ArrayList<>();
        if (getState() != ArenaState.PLAYING && getState() != ArenaState.ENDING) return;

        if (getMode() == ArenaMode.NORMAL) {
            if (getGamePlayers(TeamType.SURVIVOR).isEmpty()) {
                // slimes have compromised all survivors - they won
                // only main slime wins
                SlimePlayer mainSlime = getMainSlime();
                if (mainSlime != null) {
                    winners.add(mainSlime);
                }
                winningTeam = TeamType.SLIME;
                if (getState() != ArenaState.ENDING) endMatch();
            }
        }
        else if (getMode() == ArenaMode.CLASSIC) {
            // everyone except the slime won
            winners = getGamePlayers(TeamType.SURVIVOR);
            winningTeam = TeamType.SURVIVOR;
        }
        else if (getMode() == ArenaMode.FREEZE) {
            List<SlimePlayer> winningSurvivors = Lists.newArrayList();
            List<SlimePlayer> winningSlimes = getGamePlayers(TeamType.SLIME);
            for (SlimePlayer sp : getGamePlayers(TeamType.SURVIVOR)) {
                if (!sp.isCompromised()) {
                    winningSurvivors.add(sp);
                }
            }
            if (winningSurvivors.isEmpty()) {
                // slimes have compromised all survivors - they won
                winners = winningSlimes;
                winningTeam = TeamType.SLIME;
                if (getState() != ArenaState.ENDING) endMatch();
            }
            else {
                // there are still remaining survivors - they won
                winners = winningSurvivors;
                winningTeam = TeamType.SURVIVOR;
            }
        }
    }

    public SlimePlayer getMainSlime() {
        for (SlimePlayer sp : getGamePlayers(TeamType.SLIME)) {
            if (sp.isMainSlime()) {
                return sp;
            }
        }
        return null;
    }

    public void giveItem(SlimePlayer sp, int slot, ItemStack item) {
        sp.getPlayer().getInventory().setItem(slot, item);
    }

    public void removeItem(SlimePlayer sp, int slot) {
        sp.getPlayer().getInventory().setItem(slot, null);
    }

    public void removeItem(SlimePlayer sp, ItemStack item) {
        sp.getPlayer().getInventory().removeItem(item);
    }

    public void prepareForTeam(SlimePlayer player, TeamType type) {
        player.setTeam(type);
        Player p = player.getPlayer();
        p.getInventory().clear();

        if (type == TeamType.SLIME) {
            if (getState() == ArenaState.STARTING) {
                for (SlimePlayer sp : getGamePlayers()) {
                    SlimeSurvival.getMessages().getPlayerIsTheSlime().addVar("%player%", player.getPlayer().getName()).send(sp.getPlayer());
                }
            }
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

    public boolean isForceStart() {
        return forceStart;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public SlimeThrowerManager getSlimeThrowerManager() {
        return slimeThrowerManager;
    }

    public int getCalculatedSlimeTeamPlayers() {
        return calculatedSlimeTeamPlayers;
    }

    public ArenaWaitingCageManager getWaitingCageManager() {
        return arenaWaitingCageManager;
    }

    public SlimePlayer getPlayerFromWaitingSpawn(Loc loc) {
        for (SlimePlayer sp : gamePlayers) {
            if (sp.getWaitingSpawn() != null && sp.getWaitingSpawn().equals(loc)) {
                return sp;
            }
        }
        return null;
    }

    public int getModeVotes(ArenaMode mode) {
        int amount = 0;
        for (SlimePlayer sp : getGamePlayers()) {
            if (sp.getVotedMode() != null && sp.getVotedMode() == mode) {
                amount++;
            }
        }
        return amount;
    }

    public SignManager getSignManager() {
        return arenaSignManager;
    }

    public boolean isSlimesReleased() {
        return slimesReleased;
    }

    public void setSlimesReleased(boolean slimesReleased) {
        this.slimesReleased = slimesReleased;
    }
}
