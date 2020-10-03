package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SaveArenaCmd extends BaseCmd {

    public SaveArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "save";
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            SlimeSurvival.getMessages().getMainLobbyMustBeSet().send(sender);
            return true;
        }

        String worldName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(worldName);
        if (arena == null) {
            SlimeSurvival.getMessages().getArenaNotExisting().send(sender);
            return true;
        }


        for (Player player : Bukkit.getWorld(worldName).getPlayers()) {
            player.teleport(SlimeSurvival.getCfg().getLobbySpawn().getLocation());
        }
        arena.saveWorld();
        arena.saveArenaData();
        arena.setEditing(false);

        SlimeSurvival.getMessages().getArenaSaved().addVar("%arena%", arena.getName()).send(sender);
        return true;
    }
}
