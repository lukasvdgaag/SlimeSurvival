package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

public class JoinCmd extends BaseCmd {

    public JoinCmd() {
        type = "slimesurival";
        forcePlayer = true;
        cmdName = "join";
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

        boolean added = arena.addPlayer(player);

        if (!added) {
            sender.sendMessage(ChatColor.RED + "Could not join that game at this time.");
        }
        else {
            sender.sendMessage(ChatColor.GRAY + "You joined the game " + arena.getDisplayName());
        }

        return true;
    }

}
