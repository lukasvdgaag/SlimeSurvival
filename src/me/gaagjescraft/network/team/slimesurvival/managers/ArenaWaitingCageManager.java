package me.gaagjescraft.network.team.slimesurvival.managers;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public class ArenaWaitingCageManager {

    private SlimeArena arena;

    public ArenaWaitingCageManager(SlimeArena arena) {
        this.arena = arena;
    }

    public void removeCage(Loc loc) {
        if (loc == null) return;
        List<Location> locs = Lists.newArrayList();
        Location loc1 = loc.getLocation();

        locs.add(loc1.clone().add(0,2,0));
        locs.add(loc1.clone().add(1,0,0));
        locs.add(loc1.clone().add(0,0,1));
        locs.add(loc1.clone().add(-1,0,0));
        locs.add(loc1.clone().add(0,0,-1));

        for (Location loc2 : locs) {
            loc2.getBlock().setType(Material.AIR);
        }
    }

    public void spawnCage(Loc loc) {
        List<Location> locs = Lists.newArrayList();
        Location loc1 = loc.getLocation();

        locs.add(loc1.clone().add(0,2,0));
        locs.add(loc1.clone().add(1,0,0));
        locs.add(loc1.clone().add(0,0,1));
        locs.add(loc1.clone().add(-1,0,0));
        locs.add(loc1.clone().add(0,0,-1));

        for (Location loc2 : locs) {
            loc2.getBlock().setType(Material.BARRIER);
        }
    }

    public void spawnCages() {
        for (SlimePlayer sp : arena.getGamePlayers()) {
            spawnCage(sp.getWaitingSpawn());
        }
    }

    public SlimeArena getArena() {
        return arena;
    }

}
