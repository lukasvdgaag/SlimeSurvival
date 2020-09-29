package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import org.bukkit.ChatColor;

public class LeaveCmd extends BaseCmd {

    public LeaveCmd() {
        type = "slimesurival";
        forcePlayer = true;
        cmdName = "leave";
        alias = new String[]{"quit", "exit"};
        argLength = 1;
    }

    @Override
    public boolean run() {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(player);

        if (sp == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a game.");
            return true;
        }

        sp.getArena().leave(sp);
        sender.sendMessage(ChatColor.RED + "You left your current game.");
        return true;
    }

}
