package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;

public class LobbySpawnCmd extends BaseCmd {

    public LobbySpawnCmd() {
        type = "slimesurvival";
        forcePlayer = true;
        cmdName = "setspawn";
        alias = new String[]{"setlobby", "setlobbyspawn"};
        argLength = 1;
    }

    @Override
    public boolean run() {
        SlimeSurvival.getCfg().setLobbySpawn(Loc.fromLocation(player.getLocation()));
        SlimeSurvival.getMessages().getMainLobbySpawnSet().send(player);
        return true;
    }
}
