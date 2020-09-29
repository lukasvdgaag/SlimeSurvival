package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

public class InfoArenaCmd extends BaseCmd {

    public InfoArenaCmd() {
        type = "ssa";
        forcePlayer = false;
        cmdName = "info";
        alias = new String[]{"about"};
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

        sender.sendMessage(ChatColor.YELLOW + "---------- " + ChatColor.GOLD + "Arena Info" + ChatColor.YELLOW + " ----------");
        sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.GRAY + arena.getName());
        sender.sendMessage(ChatColor.YELLOW + "Display Name: " + ChatColor.GRAY + arena.getDisplayName());
        sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GRAY + arena.getState().name());
        sender.sendMessage(ChatColor.YELLOW + "Current players: " + ChatColor.GRAY + arena.getGamePlayers().size() + "/" + arena.getWaitingSpawns().size());
        return true;
    }
}
