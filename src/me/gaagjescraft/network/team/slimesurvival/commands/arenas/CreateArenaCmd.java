package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.*;

public class CreateArenaCmd extends BaseCmd {

    public CreateArenaCmd() {
        type = "slimearena";
        forcePlayer = true;
        cmdName = "create";
        alias = new String[]{"c"};
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            SlimeSurvival.getMessages().getMainLobbyMustBeSet().send(sender);
            return true;
        }

        String worldName = args[1];
        if (SlimeSurvival.getArena(worldName) != null) {
            SlimeSurvival.getMessages().getArenaAlreadyExists().send(sender);
            return true;
        }

        SlimeSurvival.getMessages().getGeneratingWorld().send(sender);
        boolean result = SlimeArena.createNewArena(worldName);
        if (!result) {
            sender.sendMessage(ChatColor.RED + "Something went wrong whilst creating the arena.");
            SlimeSurvival.get().removeArena(SlimeSurvival.getArena(worldName));
            return true;
        }

        SlimeSurvival.getMessages().getWorldGenerated().send(sender);
        World w = Bukkit.getWorld(worldName);
        w.getBlockAt(0, 75, 0).setType(Material.STONE);
        player.teleport(new Location(w, 0, 76, 0));
        player.setGameMode(GameMode.CREATIVE);
        return true;
    }
}
