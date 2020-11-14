package me.gaagjescraft.network.team.slimesurvival.menus;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;

public abstract class ArenaMenuManager {

    public List<SlimePlayer> viewers = Lists.newArrayList();
    public HashMap<ArenaMenuManager, Inventory> menu = new HashMap<>();
    public SlimeArena arena;

    public ArenaMenuManager(SlimeArena arena) {
        this.arena = arena;
    }
}
