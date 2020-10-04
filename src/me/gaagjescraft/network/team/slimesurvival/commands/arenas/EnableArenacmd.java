package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.ChatColor;

public class EnableArenacmd extends BaseCmd {

    public EnableArenacmd() {
        type = "slimearena";
        forcePlayer = false;
        cmdName = "enable";
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

        if (arena.getState() != ArenaState.DISABLED) {
            SlimeSurvival.getMessages().getArenaAlreadyEnabled().send(sender);
            return true;
        }


        // test if arena is set up properly

        int validStatus = SlimeUtils.isArenaValid(arena);

        switch(validStatus) {
            case 0: {
                arena.setEnabled(true);
                arena.start();
                SlimeSurvival.getMessages().getArenaEnabled().addVar("%arena%", arena.getName()).send(sender);
            }
            case 1:
                SlimeSurvival.getMessages().getArenaMisconfiguration("invalidSlimeSpawn").send(sender);
            case 2:
                SlimeSurvival.getMessages().getArenaMisconfiguration("invalidLobbySpawn").send(sender);
            case 3:
                SlimeSurvival.getMessages().getArenaMisconfiguration("invalidSpectatorSpawn").send(sender);
            case 4:
                SlimeSurvival.getMessages().getArenaMisconfiguration("notEnoughPlayerSpawns").send(sender);
            case 5:
                SlimeSurvival.getMessages().getArenaMisconfiguration("invalidMinPlayers").send(sender);
            case 6:
                SlimeSurvival.getMessages().getArenaMisconfiguration("inEditMode").addVar("%arena%", arena.getName()).send(sender);
            default:
                sender.sendMessage(ChatColor.RED + "Something went wrong whilst enabling the arena!");
        }
        return true;
    }
}
