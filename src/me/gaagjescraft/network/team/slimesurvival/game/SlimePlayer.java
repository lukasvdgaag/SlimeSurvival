package me.gaagjescraft.network.team.slimesurvival.game;

import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SlimePlayer {

    private Player player;
    private SlimeArena arena;
    private TeamType team;
    private Loc assignedWaitingSpawn;

    private Inventory inv;

    private int gameKills;

    public SlimePlayer(Player player, SlimeArena arena) {
        this.player = player;
        this.arena = arena;
        this.gameKills = 0;

        inv = Bukkit.createInventory(null, InventoryType.PLAYER, player.getName());
        storeInventory();
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
    }

}
