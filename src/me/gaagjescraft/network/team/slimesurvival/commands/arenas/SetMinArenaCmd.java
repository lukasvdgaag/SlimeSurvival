package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.ChatColor;

public class SetMinArenaCmd extends BaseCmd {

    public SetMinArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "min";
        alias = new String[]{"minplayers", "setmin", "setminplayers"};
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

        if (!SlimeUtils.isInt(args[1])) {
            sender.sendMessage(ChatColor.RED + "You must specify a valid number greater or equal to 2.");
            return true;
        }
        int amount = Integer.parseInt(args[1]);

        if (amount < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a number greater or equal to 2.");
            return true;
        }

        arena.setMinPlayers(amount);
        sender.sendMessage(ChatColor.GREEN + "The minimum amount of players to start the game is set to " + amount + " for the arena " + arena.getName());
        return true;
    }
}
