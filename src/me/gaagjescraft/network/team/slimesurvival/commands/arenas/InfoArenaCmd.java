package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;

public class InfoArenaCmd extends BaseCmd {

    public InfoArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "info";
        alias = new String[]{"about"};
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

        SlimeSurvival.getMessages().getArenaInfo()
                .addVar("%arena%", arena.getName())
                .addVar("%displayName%", arena.getDisplayName())
                .addVar("%state%", arena.getState().name())
                .addVar("%players%", arena.getGamePlayers().size() +"")
                .addVar("%maxPlayers%", arena.getWaitingSpawns().size()+"")
                .send(sender);
        return true;
    }
}
