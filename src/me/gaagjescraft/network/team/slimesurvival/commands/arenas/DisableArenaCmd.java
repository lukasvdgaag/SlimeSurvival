package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;

public class DisableArenaCmd extends BaseCmd {

    public DisableArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "disable";
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

        // test if arena is set up properly

        arena.setEnabled(false);
        arena.stop();
        SlimeSurvival.getMessages().getArenaDisabled().addVar("%arena%", arena.getName()).send(sender);
        return true;
    }
}
