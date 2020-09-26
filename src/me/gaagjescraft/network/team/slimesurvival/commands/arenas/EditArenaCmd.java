package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.ChatColor;

public class EditArenaCmd extends BaseCmd {

    public EditArenaCmd() {
        type = "ssa";
        forcePlayer = true;
        cmdName = "edit";
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (SlimeSurvival.getCfg().getLobbySpawn() == null) {
            sender.sendMessage(ChatColor.RED + "You must have set the general lobby spawn in order to perform this command.");
            return true;
        }

        String worldName = args[1];
        SlimeArena arena = SlimeSurvival.getArena(worldName);
        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "There is no arena with that name.");
            return true;
        }

        Loc teleport = arena.getSpectatorSpawn();
        if (teleport == null) {
            teleport = Loc.fromString(worldName + ":0:76:0");
        }
        arena.prepareForEditing();
        arena.setEditing(true);
        player.teleport(teleport.getLocation());


        sender.sendMessage(ChatColor.GRAY + "You are now editing the arena " + arena.getName());
        return true;
    }
}
