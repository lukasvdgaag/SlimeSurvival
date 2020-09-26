package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SaveArenaCmd extends BaseCmd {

    public SaveArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "save";
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            sender.sendMessage(ChatColor.RED + "You must have set the general lobby spawn in order to perform this command.");
            return true;
        }

        String worldName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(worldName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "There is no arena with that name.");
            return true;
        }


        for (Player player : Bukkit.getWorld(worldName).getPlayers()) {
            player.teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
        }
        arena.saveWorld();
        arena.saveArenaData();
        arena.setEditing(false);

        sender.sendMessage(ChatColor.GREEN + "Successfully saved the arena " + arena.getName());
        return true;
    }
}
