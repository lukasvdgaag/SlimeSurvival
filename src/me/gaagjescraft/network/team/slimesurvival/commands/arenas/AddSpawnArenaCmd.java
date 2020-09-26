package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class AddSpawnArenaCmd extends BaseCmd {

    public AddSpawnArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "spawn";
        alias = new String[]{"addspawn", "setspawn"};
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            sender.sendMessage(ChatColor.RED + "You must have set the general lobby spawn in order to perform this command.");
            return true;
        }

        SlimeArena arena = SlimeSurvival.getArena(player.getWorld().getName());
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "The world you're currently in does not match any arenas.");
            return true;
        }

        if (args[1].equalsIgnoreCase("spectate") || args[1].equalsIgnoreCase("spectator") || args[1].equalsIgnoreCase("spec")) {
            arena.setSpectatorSpawn(Loc.fromLocation(player.getLocation()));
            player.sendMessage(ChatColor.GREEN + "Successfully set the spectator spawn of the arena " + arena.getName() + " to your location.");
        } else if (args[1].equalsIgnoreCase("waiting") || args[1].equalsIgnoreCase("wait") ||
                args[1].equalsIgnoreCase("lobby")) {
            arena.setLobbySpawn(Loc.fromLocation(player.getLocation()));
            player.sendMessage(ChatColor.GREEN + "Successfully set the waiting lobby spawn of the arena " + arena.getName() + " to your location.");
        }
        else if (args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("game")) {
            if (arena.hasWaitingSpawn(Loc.fromLocation(player.getLocation()).shortify())) {
                player.sendMessage(ChatColor.RED + "This location already has a player spawn.");
                return true;
            }
            arena.addWaitingSpawn(Loc.fromLocation(player.getLocation()).shortify());
            player.getLocation().getBlock().setType(Material.BEACON);
            player.sendMessage(ChatColor.GREEN + "Successfully set player spawn #" + arena.getWaitingSpawns().size() + " of arena " + arena.getName() + " to your location.");
        }
        else if (args[1].equalsIgnoreCase("slime")) {
            arena.setSlimeSpawn(Loc.fromLocation(player.getLocation()).shortify());
            player.sendMessage(ChatColor.GREEN + "Successfully set the slime spawn of the arena " + arena.getName());
        }
        else {
            player.sendMessage(ChatColor.RED + "You specified an invalid spawn type. Choose from: spectate, lobby, player, slime");
        }
        return true;
    }
}
