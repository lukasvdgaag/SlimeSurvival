package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignCreationHandler implements Listener {

    @EventHandler
    public void onSignInteraction(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || !e.getClickedBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) return;

        Loc loc = Loc.fromLocation(e.getClickedBlock().getLocation());
        for (SlimeArena arena : SlimeSurvival.get().getArenas()) {
            if (arena.hasSign(loc)) {
                if (arena.addPlayer(e.getPlayer())) {
                    SlimeSurvival.getMessages().getJoinedGame().addVar("%arena%", arena.getDisplayName()).send(e.getPlayer());
                }
                return;
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        if (!e.getBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) return;

        Loc loc = Loc.fromLocation(e.getBlock().getLocation());
        for (SlimeArena arena : SlimeSurvival.get().getArenas()) {
            if (arena.hasSign(loc)) {
                arena.removeSign(loc);
                arena.saveArenaData();
                SlimeSurvival.getMessages().getSignRemoved().addVar("%arena%", arena.getDisplayName()).send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[ss]") ||
                e.getLine(0).equalsIgnoreCase("[slimesurvival]")) {
            if (!e.getBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) {
                SlimeSurvival.getMessages().getMustBeInLobby().send(e.getPlayer());
                return;
            }

            SlimeArena arena = SlimeSurvival.getArena(e.getLine(1));
            if (arena == null) {
                //invalid arena
                SlimeSurvival.getMessages().getArenaNotExisting().send(e.getPlayer());
                return;
            }

            Loc loc = Loc.fromLocation(e.getBlock().getLocation());
            if (!arena.hasSign(loc)) {
                arena.addSign(loc);
                arena.saveArenaData();
            }
            Bukkit.getScheduler().runTaskLater(SlimeSurvival.get(), ()-> {
                arena.getSignManager().update(loc);
            },20);
        }
    }

}
