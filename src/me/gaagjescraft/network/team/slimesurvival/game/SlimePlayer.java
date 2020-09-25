package me.gaagjescraft.network.team.slimesurvival.game;

import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import org.bukkit.entity.Player;

public class SlimePlayer {

    private Player player;
    private SlimeArena arena;
    private TeamType team;

    private int gameKills;

    public SlimePlayer(Player player, SlimeArena arena) {
        this.player = player;
        this.arena = arena;
        this.gameKills = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public SlimeArena getArena() {
        return arena;
    }

    public TeamType getTeam() {
        return team;
    }

    public void setTeam(TeamType team) {
        this.team = team;
    }

    public int getGameKills() {
        return gameKills;
    }

    public void setGameKills(int gameKills) {
        this.gameKills = gameKills;
    }

    public void addKill() {
        this.gameKills++;
    }
}
