package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

public class SetDisplayNameArenaCmd extends BaseCmd {

    public SetDisplayNameArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "displayname";
        alias = new String[]{"setdisplayname"};
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

        StringBuilder sb = new StringBuilder();

        for (int i=1; i<args.length;i++) {
            sb.append(args[i]).append(" ");
        }

        arena.setDisplayName(sb.toString().trim());
        sender.sendMessage(ChatColor.GREEN + "The display name of the arena " + arena.getName() + " is set to '" +
                ChatColor.translateAlternateColorCodes('&', sb.toString().trim()) + ChatColor.GREEN + "'");
        return true;
    }
}
