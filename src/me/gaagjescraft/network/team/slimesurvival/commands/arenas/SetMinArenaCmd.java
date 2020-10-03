package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;

public class SetMinArenaCmd extends BaseCmd {

    public SetMinArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "min";
        alias = new String[]{"minplayers", "setmin", "setminplayers"};
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
SlimeSurvival.getMessages().getMainLobbyMustBeSet().send(sender);
            return true;
        }

        SlimeArena arena = SlimeSurvival.getArena(player.getWorld().getName());
        if (arena == null) {
            SlimeSurvival.getMessages().getCurrentWorldMatchesNoArena().send(sender);
            return true;
        }

        if (!SlimeUtils.isInt(args[1])) {
            SlimeSurvival.getMessages().getInvalidMinPlayersNumber().send(sender);
            return true;
        }
        int amount = Integer.parseInt(args[1]);

        if (amount < 2) {
            SlimeSurvival.getMessages().getInvalidMinPlayersNumber().send(sender);
            return true;
        }

        arena.setMinPlayers(amount);
        SlimeSurvival.getMessages().getMinPlayersSet()
                .addVar("%arena%", arena.getName())
                .addVar("%amount%", amount+"")
                .send(sender);
        return true;
    }
}
