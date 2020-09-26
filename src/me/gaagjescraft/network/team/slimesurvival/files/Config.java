package me.gaagjescraft.network.team.slimesurvival.files;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;

public class Config {

    private Loc lobbySpawn;
    private int waitingLobbyTimer;
    private int timeBeforeSelectingSlimes;
    private int startingTimer;
    private int gameTimer;


    public Config() {
        load();
    }

    public void load() {
        this.lobbySpawn = Loc.fromString(SlimeSurvival.get().getConfig().getString("lobbySpawn"));
        this.waitingLobbyTimer = SlimeSurvival.get().getConfig().getInt("waitingLobbyTimer");
        this.timeBeforeSelectingSlimes = SlimeSurvival.get().getConfig().getInt("timeBeforeSelectingSlimes");
        this.startingTimer = SlimeSurvival.get().getConfig().getInt("startingTimer");
        this.gameTimer = SlimeSurvival.get().getConfig().getInt("gameTimer");
    }

    public void save() {
        SlimeSurvival.get().getConfig().set("lobbySpawn", lobbySpawn.toString());
        SlimeSurvival.get().getConfig().set("waitingLobbyTimer", waitingLobbyTimer);
        SlimeSurvival.get().getConfig().set("timeBeforeSelectingSlimes", timeBeforeSelectingSlimes);
        SlimeSurvival.get().getConfig().set("startingTimer", startingTimer);
        SlimeSurvival.get().getConfig().set("gameTimer", gameTimer);

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
}
