package me.gaagjescraft.network.team.slimesurvival.files;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;

public class Config {

    private Loc lobbySpawn;
    private int waitingLobbyTimer;
    private int timeBeforeSelectingSlimes;
    private int startingTimer;
    private int gameTimer;
    private int endTimer;
    private int leadTimer;
    private int removeThrownSlimesAfter;

    private String signDisabledBlock;
    private String signWaitingBlock;
    private String signPlayingBlock;
    private String signStartingBlock;
    private String signEndingBlock;

    private boolean spectateEnabled;


    public Config() {
        load();
    }

    public void load() {
        this.lobbySpawn = Loc.fromString(SlimeSurvival.get().getConfig().getString("lobbySpawn"));
        this.waitingLobbyTimer = SlimeSurvival.get().getConfig().getInt("waitingLobbyTimer");
        this.timeBeforeSelectingSlimes = SlimeSurvival.get().getConfig().getInt("timeBeforeSelectingSlimes");
        this.startingTimer = SlimeSurvival.get().getConfig().getInt("startingTimer");
        this.gameTimer = SlimeSurvival.get().getConfig().getInt("gameTimer");
        this.endTimer = SlimeSurvival.get().getConfig().getInt("endTimer");
        this.leadTimer = SlimeSurvival.get().getConfig().getInt("leadTimer");
        this.removeThrownSlimesAfter = SlimeSurvival.get().getConfig().getInt("removeThrownSlimesAfter");
        this.spectateEnabled = SlimeSurvival.get().getConfig().getBoolean("enableSpectate");

        this.signDisabledBlock = SlimeSurvival.get().getConfig().getString("signs.disabled");
        this.signEndingBlock = SlimeSurvival.get().getConfig().getString("signs.ending");
        this.signPlayingBlock = SlimeSurvival.get().getConfig().getString("signs.playing");
        this.signWaitingBlock = SlimeSurvival.get().getConfig().getString("signs.waiting");
        this.signStartingBlock = SlimeSurvival.get().getConfig().getString("signs.starting");

    }

    public void save() {
        SlimeSurvival.get().getConfig().set("lobbySpawn", lobbySpawn.toString());
        SlimeSurvival.get().getConfig().set("waitingLobbyTimer", waitingLobbyTimer);
        SlimeSurvival.get().getConfig().set("timeBeforeSelectingSlimes", timeBeforeSelectingSlimes);
        SlimeSurvival.get().getConfig().set("startingTimer", startingTimer);
        SlimeSurvival.get().getConfig().set("gameTimer", gameTimer);
        SlimeSurvival.get().getConfig().set("endTimer", endTimer);
        SlimeSurvival.get().getConfig().set("leadTimer", leadTimer);
        SlimeSurvival.get().getConfig().set("removeThrownSlimesAfter", removeThrownSlimesAfter);
        SlimeSurvival.get().getConfig().set("enableSpectate", spectateEnabled);

        SlimeSurvival.get().getConfig().set("signs.disabled", signDisabledBlock);
        SlimeSurvival.get().getConfig().set("signs.ending", signEndingBlock);
        SlimeSurvival.get().getConfig().set("signs.playing", signPlayingBlock);
        SlimeSurvival.get().getConfig().set("signs.waiting", signWaitingBlock);
        SlimeSurvival.get().getConfig().set("signs.starting", signStartingBlock);

        SlimeSurvival.get().saveConfig();
    }

    public Loc getLobbySpawn() {
        return lobbySpawn;
    }
    public void setLobbySpawn(Loc loc) {
        this.lobbySpawn = loc;
        save();
    }

    public int getWaitingLobbyTimer() {
        return waitingLobbyTimer;
    }

    public int getTimeBeforeSelectingSlimes() {
        return timeBeforeSelectingSlimes;
    }

    public int getStartingTimer() {
        return startingTimer;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public boolean isSpectateEnabled() {
        return spectateEnabled;
    }

    public int getEndTimer() {
        return endTimer;
    }

    public int getLeadTimer() {
        return leadTimer;
    }

    public String getSignDisabledBlock() {
        return signDisabledBlock;
    }

    public String getSignWaitingBlock() {
        return signWaitingBlock;
    }

    public String getSignPlayingBlock() {
        return signPlayingBlock;
    }

    public String getSignStartingBlock() {
        return signStartingBlock;
    }

    public String getSignEndingBlock() {
        return signEndingBlock;
    }

    public int getRemoveThrownSlimesAfter() {
        return removeThrownSlimesAfter;
    }
}
