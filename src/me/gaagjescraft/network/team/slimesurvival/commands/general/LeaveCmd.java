package me.gaagjescraft.network.team.slimesurvival.commands.general;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;

public class LeaveCmd extends BaseCmd {

    public LeaveCmd() {
        type = "slimesurvival";
        forcePlayer = true;
        cmdName = "leave";
        alias = new String[]{"quit", "exit"};
        argLength = 1;
    }

    @Override
    public boolean run() {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(player);

        if (sp == null) {
            SlimeSurvival.getMessages().getMustBeInGame().send(sender);
            return true;
        }

        sp.getArena().leave(sp);
        SlimeSurvival.getMessages().getLeftGame().send(sender);
        return true;
    }

}
