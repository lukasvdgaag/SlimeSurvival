package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

public class StartCmd extends BaseCmd {

    public StartCmd() {
        type = "slimesurival";
        forcePlayer = true;
        cmdName = "start";
        argLength = 2;
    }

    @Override
    public boolean run() {
        String arenaName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(arenaName);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "There is no arena with that name.");
            return true;
        }

        if (arena.getState() != ArenaState.WAITING) {
            sender.sendMessage(ChatColor.RED + "That arena is already running / is disabled.");
            return true;
        }

        arena.setForceStart(true);
        sender.sendMessage(ChatColor.GREEN + "You successfully forced the arena " + arena.getName() + " to start.");
        return true;
    }

}
