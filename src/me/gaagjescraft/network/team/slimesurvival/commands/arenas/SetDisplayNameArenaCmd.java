package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;

public class SetDisplayNameArenaCmd extends BaseCmd {

    public SetDisplayNameArenaCmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "displayname";
        alias = new String[]{"setdisplayname"};
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

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        arena.setDisplayName(sb.toString().trim());
        SlimeSurvival.getMessages().getArenaDisplayNameSet()
                .addVar("%arena%", arena.getName())
                .addVar("%displayName%", sb.toString().trim())
                .send(sender);
        return true;
    }
}
