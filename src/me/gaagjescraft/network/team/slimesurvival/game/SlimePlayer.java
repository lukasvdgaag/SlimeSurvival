package me.gaagjescraft.network.team.slimesurvival.game;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.managers.SlimeBoard;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;

public class SlimePlayer {

    private static HashMap<Player, SlimeBoard> scoreboards = new HashMap<>();

    private Player player;
    private SlimeArena arena;
    private TeamType team;
    private Loc assignedWaitingSpawn;
    private boolean isCompromised;
    private boolean isMainSlime;

    private Inventory inv;

    private int gameKills;

    public SlimePlayer(Player player, SlimeArena arena) {
        this.player = player;
        this.arena = arena;
        this.gameKills = 0;
        this.team = null;
        this.isCompromised = false;
        this.isMainSlime = false;

        inv = Bukkit.createInventory(null, InventoryType.PLAYER, player.getName());
        storeInventory();
    }

    public boolean isMainSlime() {
        return isMainSlime;
    }

    public void setMainSlime(boolean mainSlime) {
        isMainSlime = mainSlime;
    }

    public boolean isCompromised() {
        return isCompromised;
    }

    public void setCompromised(boolean compromised) {
        isCompromised = compromised;
    }

    public void setAssignedWaitingSpawn(Loc waitingSpawn) {
        this.assignedWaitingSpawn = waitingSpawn;
    }

    public Loc getWaitingSpawn() {
        return assignedWaitingSpawn;
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

    public void prepareInventoryForGame() {
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        if (arena.getState() == ArenaState.WAITING) {
            player.getInventory().setItem(0, ItemsManager.ITEM_VOTE_MODE);
            player.getInventory().setItem(8, ItemsManager.ITEM_LEAVE);
        }
    }

    public void storeInventory() {
        inv.setContents(player.getInventory().getContents());
    }

    public void restoreInventory() {
        player.getInventory().setContents(inv.getContents());
    }

    public void repair() {
        restoreInventory();
        resetScoreboard();
    }

    public void updateScoreboard() {
        List<String> lines = Lists.newArrayList();
        if (arena.getState() == ArenaState.WAITING) {
            if (arena.getGamePlayers().size() >= arena.getMinPlayers() || arena.isForceStart()) {
                lines = SlimeSurvival.getMessages().getScoreboardFormat("waitboard-countdown")
                        .addVar("{arena}", arena.getDisplayName())
                        .addVar("{players}", arena.getGamePlayers().size() + "")
                        .addVar("{maxplayers}", arena.getWaitingSpawns().size() + "")
                        .addVar("{mode}", arena.getMode().name())
                        .addVar("{timer}", arena.getTimer() + "")
                        .addVar("{playersneeded}", (arena.getGamePlayers().size()>=arena.getMinPlayers()? "0" : (arena.getMinPlayers()-arena.getGamePlayers().size())+""))
                        .getMessage();
            }
            else {
                lines = SlimeSurvival.getMessages().getScoreboardFormat("waitboard")
                        .addVar("{arena}", arena.getDisplayName())
                        .addVar("{players}", arena.getGamePlayers().size() + "")
                        .addVar("{maxplayers}", arena.getWaitingSpawns().size() + "")
                        .addVar("{mode}", arena.getMode().name())
                        .addVar("{timer}", arena.getTimer() + "")
                        .addVar("{playersneeded}", (arena.getGamePlayers().size()>=arena.getMinPlayers()? "0" : (arena.getMinPlayers()-arena.getGamePlayers().size())+""))
                        .getMessage();
            }
        }
        else if (arena.getState() == ArenaState.STARTING || arena.getState() == ArenaState.PLAYING) {
            lines = SlimeSurvival.getMessages().getScoreboardFormat("playboard")
                    .addVar("{arena}", arena.getDisplayName())
                    .addVar("{players}", arena.getGamePlayers().size()+"")
                    .addVar("{maxplayers}", arena.getWaitingSpawns().size()+"")
                    .addVar("{mode}", arena.getMode().name())
                    .addVar("{timer}", arena.getTimer()+"")
                    .addVar("{playersneeded}", (arena.getGamePlayers().size()>=arena.getMinPlayers()? "0" : (arena.getMinPlayers()-arena.getGamePlayers().size())+""))
                    .getMessage();
        }
        else {
            resetScoreboard();
            return;
        }

        List<String> actualLines = Lists.newArrayList();

        for (int i = 0; i<lines.size();i++) {
            String line = lines.get(i);
            if (line.equals("{slimes}")) {
                for (SlimePlayer sp : arena.getGamePlayers(TeamType.SLIME)) {
                    actualLines.add("§2" + sp.getPlayer().getName());
                }
            }
            else if (line.equals("{survivors}")) {
                for (SlimePlayer sp : arena.getNonSlimeGamePlayers()) {
                    // todo add mode check and compromised check
                    actualLines.add("§7" + sp.getPlayer().getName());
                }
            }
            else {
                actualLines.add(lines.get(i));
            }
        }

        SlimeBoard board = scoreboards.get(player);

        if (board == null || board.getLinecount() != actualLines.size()-1) {
            resetScoreboard();
            board = null;
        }

        if (board == null) {
            board = new SlimeBoard(player, actualLines.size()-1);
            scoreboards.put(player, board);
        }

        board.setTitle(actualLines.get(0));
        for (int i=1;i<actualLines.size();i++){
            if (!board.getLine(i-1).equals(actualLines.get(i)))
                board.setLine(i-1, actualLines.get(i));
        }

    }

    private void resetScoreboard() {
        SlimeBoard scoreboard = scoreboards.get(player);
        if (scoreboard != null) {
            scoreboards.remove(player);
            for (Objective objective : scoreboard.board.getObjectives()) {
                if (objective != null) {
                    objective.unregister();
                }
            }
            for (Team team : scoreboard.board.getTeams()) {
                if (team != null) team.unregister();
            }
        }
        scoreboards.remove(player);
    }

}
