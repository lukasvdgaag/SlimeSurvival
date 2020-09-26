package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SpawnRemovalHandler implements Listener {

    @EventHandler
    public void onSpawnRemoval(BlockBreakEvent e) {
        SlimeArena arena = SlimeSurvival.getArena(e.getPlayer().getWorld().getName());
        if (arena == null) {
            e.getPlayer().sendMessage("arena is null");
            return;
        } else if (!arena.isEditing()) {
            e.getPlayer().sendMessage("arena is not in edit mode");
            return;
        }

        e.getPlayer().sendMessage("waitingspawns size: " + arena.getWaitingSpawns().size());

        Loc current = Loc.fromLocation(e.getBlock().getLocation()).shortify();
        e.getPlayer().sendMessage("current block: " + current.getWorld() + ", " + current.getX() + ", " + current.getY() + ", " + current.getZ());


        for (int i=0;i<arena.getWaitingSpawns().size();i++) {
            Loc loc = arena.getWaitingSpawns().get(i);
            e.getPlayer().sendMessage("waitingspawn loc: " + loc.getWorld() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
            if (loc.shortify().matches(current)) {
                arena.removeWaitingSpawn(loc);
                e.getPlayer().sendMessage(ChatColor.RED + "Successfully removed waiting spawn #" + (i+1) + " from arena " + arena.getName());
                return;
            }
        }
        e.getPlayer().sendMessage("no spawns were found");
    }

}
