package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.ChatColor;

public class EnableArenacmd extends BaseCmd {

    public EnableArenacmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "enable";
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

        if (arena.getState() != ArenaState.DISABLED) {
            sender.sendMessage(ChatColor.RED + "That arena is already enabled.");
            return true;
        }


        // test if arena is set up properly

        int validStatus = SlimeUtils.isArenaValid(arena);

        if (validStatus == 0) {
            arena.setEnabled(true);
            arena.start();
            sender.sendMessage(ChatColor.GREEN + "Successfully enabled the arena " + arena.getName());
        }

        else if (validStatus == 1) {
            sender.sendMessage(ChatColor.RED + "Slime spawn is not set! Set it with \"/ssa spawn slime\".");
        }
        else if (validStatus == 2) {
            sender.sendMessage(ChatColor.RED + "Game lobby spawn is not set! Set it with \"/ssa spawn lobby\".");
        }
        else if (validStatus == 3) {
            sender.sendMessage(ChatColor.RED + "Spectator spawn is not set! Set it with \"/ssa spawn spectator\".\n" +
                    "If you want to disable spectate mode, set the option 'enableSpectate' in the config.yml to 'false'.");
        }
        else if (validStatus == 4) {
            sender.sendMessage(ChatColor.RED + "Not enough player spawns found! You must have at least 2 player spawns.");
        }
        else if (validStatus == 5) {
            sender.sendMessage(ChatColor.RED + "Min players is too low! The minimum amount of player must be 2 or greater.");
        }
        else if (validStatus == 6) {
            sender.sendMessage(ChatColor.RED + "Arena is in edit mode! Save the arena first using \"/ssa save " + arena.getName() + "\".");
        }
        return true;
    }
}
