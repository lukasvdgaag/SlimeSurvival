package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.*;

public class CreateArenaCmd extends BaseCmd {

    public CreateArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "create";
        alias = new String[]{"c"};
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            sender.sendMessage(ChatColor.RED + "You must have set the general lobby spawn in order to perform this command.");
            return true;
        }

        String worldName = args[1];
        if (SlimeSurvival.getArena(worldName) != null) {
            sender.sendMessage(ChatColor.RED + "There already is an arena with that name.");
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Now generating the arena world...");
        boolean result = SlimeArena.createNewArena(worldName);
        if (!result) {
            sender.sendMessage(ChatColor.RED + "Something went wrong whilst creating the arena.");
            SlimeSurvival.get().removeArena(SlimeSurvival.getArena(worldName));
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Arena world successfully generated! Teleporting...");
        World w = Bukkit.getWorld(worldName);
        w.getBlockAt(0, 75, 0).setType(Material.STONE);
        player.teleport(new Location(w, 0, 76, 0));
        player.setGameMode(GameMode.CREATIVE);


        return true;
    }
}
