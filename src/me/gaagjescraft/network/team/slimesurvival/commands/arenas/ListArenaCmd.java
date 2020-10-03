package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.ChatColor;

import java.util.List;

public class ListArenaCmd extends BaseCmd {

    public ListArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "list";
        alias = new String[]{"arenas"};
        argLength = 1;
    }

    @Override
    public boolean run() {
        List<SlimeArena> arenas = SlimeSurvival.get().getArenas();

        SlimeSurvival.getMessages().getArenaListHeader().send(sender);

        if (arenas.size() == 0) {
            SlimeSurvival.getMessages().getNoArenasFound().send(sender);
            return true;
        }

        for (SlimeArena arena : arenas) {
            ArenaState state = arena.getState();
            String a = ChatColor.RED + "Disabled";
            if (state == ArenaState.PLAYING) a = ChatColor.GREEN + "Playing";
            else if (state == ArenaState.STARTING) a = ChatColor.BLUE + "Starting";
            else if (state == ArenaState.WAITING) a = ChatColor.GREEN + "Waiting";
            sender.sendMessage(ChatColor.GRAY + arena.getName() + ChatColor.WHITE + " - " + ChatColor.AQUA + arena.getDisplayName() + ChatColor.WHITE + " - " + a);
        }

        return true;
    }
}
