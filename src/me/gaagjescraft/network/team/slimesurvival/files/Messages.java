package me.gaagjescraft.network.team.slimesurvival.files;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Messages {

    private FileConfiguration fc;

    private List<String> normalGameDescription;
    private List<String> freezeGameDescription;
    private String startSlimeSelectionTitle;
    private String releaseSurvivorsTitle;
    private String releaseSlimesTitle;
    private String countdownSlimeSelectionTeleportation;
    private String selectingSlimeAmountMessage;
    private String selectingSlimeAmountTitle;
    private String startSlimeSelectionCountdownTitle;
    private String countdownSlimeSelectionTeleportationTitle;
    private String playerIsTheSlime;
    private String gameEnded;
    private String noCommandPermission;
    private String mustBePlayer;
    private String mainLobbyMustBeSet;
    private String currentWorldMatchesNoArena;
    private String mainLobbySpawnSet;
    private String pluginReloaded;
    private String signCreated;
    private String signRemoved;
    private String mustBeInLobby;

    private String playerGotSlimed;
    private String playerParalysed;
    private String playerGotTagged;
    private String releaseSlimesForSurvivors;
    private String releaseSlimesForSurvivorsTitle;

    private String alreadyInGame;
    private String gameIsFull;
    private String joinedGame;
    private String mustBeInGame;
    private String leftGame;

    private String arenaSpawnInUse;
    private String invalidSpawnType;
    private String arenaSpawnSet;
    private String arenaSpawnAdded;
    private String arenaSpawnRemoved;
    private String arenaAlreadyExists;
    private String arenaNotExisting;
    private String generatingWorld;
    private String worldGenerated;
    private String editingArena;
    private String arenaAlreadyEnabled;
    private String arenaEnabled;
    private List<String> arenaInfo;
    private String arenaListHeader;
    private String noArenasFound;
    private String arenaSaved;
    private String arenaDisplayNameSet;
    private String invalidMinPlayersNumber;
    private String minPlayersSet;
    private String arenaDisabled;

    public Messages() {
        load();
        save();
        load();
    }

    public void load() {
        File file = new File(SlimeSurvival.get().getDataFolder(), "messages.yml");
        if (!file.exists()) SlimeSurvival.get().saveResource("messages.yml", false);

        SlimeUtils.copyDefaults(file, "messages.yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
        this.fc = fc;

        this.normalGameDescription = fc.getStringList("normalGameDescription");
        this.freezeGameDescription = fc.getStringList("freezeGameDescription");
        this.countdownSlimeSelectionTeleportation = fc.getString("countdownSlimeSelectionTeleportation");
        this.selectingSlimeAmountMessage = fc.getString("selectingSlimeAmount");
        this.playerIsTheSlime = fc.getString("playerIsTheSlime");
        this.gameEnded = fc.getString("gameEnded");
        this.noCommandPermission = fc.getString("noCommandPermission");
        this.mustBePlayer = fc.getString("mustBePlayer");
        this.mainLobbyMustBeSet = fc.getString("mainLobbyMustBeSet");
        this.currentWorldMatchesNoArena = fc.getString("currentWorldMatchesNoArena");
        this.alreadyInGame = fc.getString("alreadyInGame");
        this.gameIsFull = fc.getString("gameIsFull");
        this.joinedGame = fc.getString("joinedGame");
        this.mustBeInGame = fc.getString("mustBeInGame");
        this.leftGame = fc.getString("leftGame");
        this.mainLobbySpawnSet = fc.getString("mainLobbySpawnSet");
        this.pluginReloaded = fc.getString("pluginReloaded");
        this.signCreated = fc.getString("signCreated");
        this.signRemoved = fc.getString("signRemoved");
        this.mustBeInLobby = fc.getString("mustBeInLobby");

        this.playerGotSlimed = fc.getString("playerGotSlimed");
        this.playerParalysed = fc.getString("playerParalysed");
        this.playerGotTagged = fc.getString("playerGotTagged");
        this.releaseSlimesForSurvivors = fc.getString("releaseSlimesForSurvivors");

        this.arenaSpawnInUse = fc.getString("arenas.spawnLocationInUse");
        this.invalidSpawnType = fc.getString("arenas.invalidSpawnType");
        this.arenaSpawnSet = fc.getString("arenas.spawnSet");
        this.arenaSpawnAdded = fc.getString("arenas.spawnAdded");
        this.arenaSpawnRemoved = fc.getString("arenas.spawnRemoved");
        this.arenaAlreadyExists = fc.getString("arenas.alreadyExists");
        this.arenaNotExisting = fc.getString("arenas.notExisting");
        this.generatingWorld = fc.getString("arenas.generatingWorld");
        this.worldGenerated = fc.getString("arenas.worldGenerated");
        this.editingArena = fc.getString("arenas.editingArena");
        this.arenaAlreadyEnabled = fc.getString("arenas.alreadyEnabled");
        this.arenaEnabled = fc.getString("arenas.enabled");
        this.arenaInfo = fc.getStringList("arenas.arenaInfo");
        this.arenaListHeader = fc.getString("arenas.arenaListHeader");
        this.noArenasFound = fc.getString("arenas.noArenasFound");
        this.arenaSaved = fc.getString("arenas.saved");
        this.arenaDisplayNameSet = fc.getString("arenas.displayNameSet");
        this.invalidMinPlayersNumber = fc.getString("arenas.invalidMinPlayersNumber");
        this.minPlayersSet = fc.getString("arenas.minPlayersSet");
        this.arenaDisabled = fc.getString("arenas.disabled");

        this.startSlimeSelectionTitle = fc.getString("titles.startSlimeSelection");
        this.releaseSlimesTitle = fc.getString("titles.releaseSlimes");
        this.releaseSurvivorsTitle = fc.getString("titles.releaseSurvivors");
        this.selectingSlimeAmountTitle = fc.getString("titles.selectingSlimeAmount");
        this.startSlimeSelectionCountdownTitle = fc.getString("titles.startSlimeSelectionCountdown");
        this.countdownSlimeSelectionTeleportationTitle = fc.getString("titles.countdownSlimeSelectionTeleportation");
        this.releaseSlimesForSurvivorsTitle = fc.getString("titles.releaseSlimesForSurvivors");

    }

    public void save() {
        File file = new File(SlimeSurvival.get().getDataFolder(), "messages.yml");
        if (!file.exists()) SlimeSurvival.get().saveResource("messages.yml", false);

        SlimeUtils.copyDefaults(file, "messages.yml");
        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

        fc.set("normalGameDescription", normalGameDescription);
        fc.set("freezeGameDescription", freezeGameDescription);
        fc.set("countdownSlimeSelectionTeleportation", countdownSlimeSelectionTeleportation);
        fc.set("selectingSlimeAmount", selectingSlimeAmountMessage);
        fc.set("playerIsTheSlime", playerIsTheSlime);
        fc.set("gameEnded", gameEnded);
        fc.set("noCommandPermission", noCommandPermission);
        fc.set("mustBePlayer", mustBePlayer);
        fc.set("mainLobbyMustBeSet", mainLobbyMustBeSet);
        fc.set("currentWorldMatchesNoArena", currentWorldMatchesNoArena);
        fc.set("alreadyInGame", alreadyInGame);
        fc.set("gameIsFull", gameIsFull);
        fc.set("joinedGame", joinedGame);
        fc.set("mustBeInGame", mustBeInGame);
        fc.set("leftGame", leftGame);
        fc.set("mainLobbySpawnSet", mainLobbySpawnSet);
        fc.set("pluginReloaded", pluginReloaded);
        fc.set("signCreated", signCreated);
        fc.set("signRemoved", signRemoved);
        fc.set("mustBeInLobby", mustBeInLobby);

        fc.set("playerGotSlimed", playerGotSlimed);
        fc.set("playerParalysed", playerParalysed);
        fc.set("playerGotTagged", playerGotTagged);
        fc.set("releaseSlimesForSurvivors", releaseSlimesForSurvivors);

        fc.set("arenas.spawnLocationInUse", arenaSpawnInUse);
        fc.set("arenas.invalidSpawnType", invalidSpawnType);
        fc.set("arenas.spawnSet", arenaSpawnSet);
        fc.set("arenas.spawnAdded", arenaSpawnAdded);
        fc.set("arenas.spawnRemoved", arenaSpawnRemoved);
        fc.set("arenas.alreadyExists", arenaAlreadyExists);
        fc.set("arenas.notExisting", arenaNotExisting);
        fc.set("arenas.generatingWorld", generatingWorld);
        fc.set("arenas.worldGenerated", worldGenerated);
        fc.set("arenas.editingArena", editingArena);
        fc.set("arenas.alreadyEnabled", arenaAlreadyEnabled);
        fc.set("arenas.enabled", arenaEnabled);
        fc.set("arenas.arenaInfo", arenaInfo);
        fc.set("arenas.arenaListHeader", arenaListHeader);
        fc.set("arenas.noArenasFound", noArenasFound);
        fc.set("arenas.saved", arenaSaved);
        fc.set("arenas.displayNameSet", arenaDisplayNameSet);
        fc.set("arenas.invalidMinPlayersNumber", invalidMinPlayersNumber);
        fc.set("arenas.minPlayersSet", minPlayersSet);
        fc.set("arenas.disabled", arenaDisabled);

        fc.set("titles.startSlimeSelection", startSlimeSelectionTitle);
        fc.set("titles.releaseSlimes", releaseSlimesTitle);
        fc.set("titles.releaseSurvivors", releaseSurvivorsTitle);
        fc.set("titles.selectingSlimeAmount", selectingSlimeAmountTitle);
        fc.set("titles.startSlimeSelectionCountdown", startSlimeSelectionCountdownTitle);
        fc.set("titles.countdownSlimeSelectionTeleportation", countdownSlimeSelectionTeleportationTitle);
        fc.set("titles.releaseSlimesForSurvivors", releaseSlimesForSurvivorsTitle);

        try {
            fc.save(file);
            this.fc = fc;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getFile() {
        return fc;
    }

    public MSG getCommandDescription(String section, String cmd) {
        return new MSG(fc.getString("commands." + section + "." + cmd, ""));
    }

    public MSG getArenaMisconfiguration(String id) {
        return new MSG(fc.getString("arenas.misconfigurations." + id, ""));
    }

    public MSG getScoreboardFormat(String format) {
        return new MSG(fc.getStringList("scoreboards."+format));
    }

    public MSG getSignFormat(String format) {
        return new MSG(fc.getStringList("signs."+format));
    }


    public MSG getNormalGameDescription() {
        return new MSG(normalGameDescription);
    }

    public MSG getFreezeGameDescription() {
        return new MSG(freezeGameDescription);
    }

    public MSG getReleaseSlimesTitle() {
        return new MSG(releaseSlimesTitle);
    }

    public MSG getReleaseSurvivorsTitle() {
        return new MSG(releaseSurvivorsTitle);
    }

    public MSG getStartSlimeSelectionTitle() {
        return new MSG(startSlimeSelectionTitle);
    }

    public MSG getCountdownSlimeSelectionTeleportation() {
        return new MSG(countdownSlimeSelectionTeleportation);
    }

    public MSG getSelectingSlimeAmountMessage() {
        return new MSG(selectingSlimeAmountMessage);
    }

    public MSG getSelectingSlimeAmountTitle() {
        return new MSG(selectingSlimeAmountTitle);
    }

    public MSG getStartSlimeSelectionCountdownTitle() {
        return new MSG(startSlimeSelectionCountdownTitle);
    }

    public MSG getCountdownSlimeSelectionTeleportationTitle() {
        return new MSG(countdownSlimeSelectionTeleportationTitle);
    }

    public MSG getPlayerIsTheSlime() {
        return new MSG(playerIsTheSlime);
    }

    public MSG getGameEnded() {
        return new MSG(gameEnded);
    }

    public MSG getNoCommandPermission() {
        return new MSG(noCommandPermission);
    }

    public MSG getMustBePlayer() {
        return new MSG(mustBePlayer);
    }

    public MSG getMainLobbyMustBeSet() {
        return new MSG(mainLobbyMustBeSet);
    }

    public MSG getCurrentWorldMatchesNoArena() {
        return new MSG(currentWorldMatchesNoArena);
    }

    public MSG getArenaSpawnInUse() {
        return new MSG(arenaSpawnInUse);
    }

    public MSG getInvalidSpawnType() {
        return new MSG(invalidSpawnType);
    }

    public MSG getArenaSpawnSet() {
        return new MSG(arenaSpawnSet);
    }

    public MSG getArenaSpawnAdded() {
        return new MSG(arenaSpawnAdded);
    }

    public MSG getArenaSpawnRemoved() {
        return new MSG(arenaSpawnRemoved);
    }

    public MSG getArenaAlreadyExists() {
        return new MSG(arenaAlreadyExists);
    }

    public MSG getArenaNotExisting() {
        return new MSG(arenaNotExisting);
    }

    public MSG getGeneratingWorld() {
        return new MSG(generatingWorld);
    }

    public MSG getWorldGenerated() {
        return new MSG(worldGenerated);
    }

    public MSG getEditingArena() {
        return new MSG(editingArena);
    }

    public MSG getArenaAlreadyEnabled() {
        return new MSG(arenaAlreadyEnabled);
    }

    public MSG getArenaEnabled() {
        return new MSG(arenaEnabled);
    }

    public MSG getArenaInfo() {
        return new MSG(arenaInfo);
    }

    public MSG getArenaListHeader() {
        return new MSG(arenaListHeader);
    }

    public MSG getNoArenasFound() {
        return new MSG(noArenasFound);
    }

    public MSG getArenaSaved() {
        return new MSG(arenaSaved);
    }

    public MSG getArenaDisplayNameSet() {
        return new MSG(arenaDisplayNameSet);
    }

    public MSG getInvalidMinPlayersNumber() {
        return new MSG(invalidMinPlayersNumber);
    }

    public MSG getMinPlayersSet() {
        return new MSG(minPlayersSet);
    }

    public MSG getAlreadyInGame() {
        return new MSG(alreadyInGame);
    }

    public MSG getJoinedGame() {
        return new MSG(joinedGame);
    }

    public MSG getGameIsFull() {
        return new MSG(gameIsFull);
    }

    public MSG getMustBeInGame() {
        return new MSG(mustBeInGame);
    }

    public MSG getLeftGame() {
        return new MSG(leftGame);
    }

    public MSG getMainLobbySpawnSet() {
        return new MSG(mainLobbySpawnSet);
    }

    public MSG getPluginReloaded() {
        return new MSG(pluginReloaded);
    }

    public MSG getSignCreated() { return new MSG(signCreated); }

    public MSG getSignRemoved() { return new MSG(signRemoved); }

    public MSG getMustBeInLobby() { return new MSG(mustBeInLobby); }

    public MSG getArenaDisabled() { return new MSG(arenaDisabled); }

    public MSG getPlayerGotSlimed() { return new MSG(playerGotSlimed); }

    public MSG getPlayerGotParalysed() { return new MSG(playerParalysed); }

    public MSG getPlayerGotTagged() { return new MSG(playerGotTagged); }

    public MSG getReleaseSlimesForSurvivors() { return new MSG(releaseSlimesForSurvivors); }

    public MSG getReleaseSlimesForSurvivorsTitle() { return new MSG(releaseSlimesForSurvivorsTitle); }

    public class MSG {
        List<String> message;
        CommandSender player;

        public MSG(String msg) {
            this.message = Lists.newArrayList(SlimeUtils.c(msg));
        }

        public MSG(List<String> msg) {
            this.message = SlimeUtils.c(msg);
        }

        public void setPlayer(CommandSender p) {
            this.player = p;
        }

        public List<String> getMessage() {
            return this.message;
        }

        public MSG addVar(String placeholder, String replacement) {
            for (int i = 0; i < message.size(); i++) {
                message.set(i, message.get(i).replace(placeholder, replacement));
            }
            return this;
        }

        public MSG send(CommandSender player) {
            for (String a : message) {
                player.sendMessage(SlimeUtils.c(a).replace("\\\\n", "\n"));
            }
            return this;
        }

        public MSG sendTitle(Player player, int fadeIn, int stay, int fadeOut) {
            SlimeUtils.sendTitle(player, SlimeUtils.c(message.get(0)), fadeIn, stay, fadeOut);
            return this;
        }
    }
}
