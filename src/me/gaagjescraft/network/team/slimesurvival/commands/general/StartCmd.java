package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

public class StartCmd extends BaseCmd {

    public StartCmd() {
        type = "slimesurvival";
        forcePlayer = true;
        cmdName = "start";
        argLength = 2;
    }

    @Override
    public boolean run() {
        String arenaName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(arenaName);

        if (arena == null) {
            SlimeSurvival.getMessages().getArenaNotExisting().send(sender);
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
