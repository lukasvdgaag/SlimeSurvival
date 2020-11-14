package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;

public class JoinCmd extends BaseCmd {

    public JoinCmd() {
        type = "slimesurvival";
        forcePlayer = true;
        cmdName = "join";
        argLength = 2;
    }

    @Override
    public boolean run() {
        String arenaName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(arenaName);

        if (arena == null) {
            SlimeSurvival.getMessages().getArenaNotExisting().send(sender);
            return true;
        }

        if (SlimeSurvival.getSlimePlayer(player) != null) {
            SlimeSurvival.getMessages().getAlreadyInGame().send(player);
            return true;
        }


        boolean added = arena.addPlayer(player);

        if (!added) {
            SlimeSurvival.getMessages().getGameIsFull().send(player);
        }

        return true;
    }

}
