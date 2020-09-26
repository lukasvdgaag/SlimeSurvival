package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.ChatColor;

public class LobbySpawnCmd extends BaseCmd {

    public LobbySpawnCmd() {
        type = "slimesurival";
        forcePlayer = true;
        cmdName = "setspawn";
        alias = new String[]{"setlobby", "setlobbyspawn"};
        argLength = 1;
    }

    @Override
    public boolean run() {
        SlimeSurvival.getCfg().setLobbySpawn(Loc.fromLocation(player.getLocation()));
        player.sendMessage(ChatColor.GREEN + "Successfully set the lobby spawn to your location.");
        return true;
    }
}
