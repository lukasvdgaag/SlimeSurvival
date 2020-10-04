package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class EditArenaCmd extends BaseCmd {

    public EditArenaCmd() {
        type = "slimearena";
        forcePlayer = true;
        cmdName = "edit";
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

        arena.setEnabled(false);
        arena.stop();
        Loc teleport = arena.getSpectatorSpawn();
        if (teleport == null) {
            teleport = Loc.fromString(worldName + ":0:76:0");
        }
        arena.setEditing(true);
        player.teleport(teleport.getLocation());
        Bukkit.getScheduler().runTaskLater(SlimeSurvival.get(), () -> {
            player.setGameMode(GameMode.CREATIVE);
            arena.prepareForEditing();
        },20);


        SlimeSurvival.getMessages().getEditingArena().addVar("%arena%", arena.getName()).send(sender);
        return true;
    }
}
