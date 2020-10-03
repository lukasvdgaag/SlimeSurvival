package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;

public class ReloadCmd extends BaseCmd {

    public ReloadCmd() {
        type = "slimesurvival";
        forcePlayer = false;
        cmdName = "reload";
        argLength = 1;
    }

    @Override
    public boolean run() {
        SlimeSurvival.get().onDisable();
        SlimeSurvival.get().onEnable();
        SlimeSurvival.getMessages().getPluginReloaded().send(sender);
        return true;
    }
}
