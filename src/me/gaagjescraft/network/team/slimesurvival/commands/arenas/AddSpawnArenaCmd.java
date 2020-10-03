package me.gaagjescraft.network.team.slimesurvival.commands.arenas;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.commands.BaseCmd;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Material;

public class AddSpawnArenaCmd extends BaseCmd {

    public AddSpawnArenaCmd() {
        type = "slimearena";
        forcePlayer = true;
        cmdName = "spawn";
        alias = new String[]{"addspawn", "setspawn"};
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

        if (args[1].equalsIgnoreCase("spectate") || args[1].equalsIgnoreCase("spectator") || args[1].equalsIgnoreCase("spec")) {
            arena.setSpectatorSpawn(Loc.fromLocation(player.getLocation()));
            SlimeSurvival.getMessages().getArenaSpawnSet()
                    .addVar("%type%", "spectator")
                    .addVar("%arena%", arena.getName())
                    .send(sender);
        } else if (args[1].equalsIgnoreCase("waiting") || args[1].equalsIgnoreCase("wait") ||
                args[1].equalsIgnoreCase("lobby")) {
            arena.setLobbySpawn(Loc.fromLocation(player.getLocation()));
            SlimeSurvival.getMessages().getArenaSpawnSet()
                    .addVar("%type%", "lobby")
                    .addVar("%arena%", arena.getName())
                    .send(sender);
        }
        else if (args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("game")) {
            if (arena.hasWaitingSpawn(Loc.fromLocation(player.getLocation()).shortify())) {
                SlimeSurvival.getMessages().getArenaSpawnInUse().send(player);
                return true;
            }
            arena.addWaitingSpawn(Loc.fromLocation(player.getLocation()).shortify());
            player.getLocation().getBlock().setType(Material.BEACON);
            SlimeSurvival.getMessages().getArenaSpawnAdded()
                    .addVar("%spawn%", arena.getWaitingSpawns().size()+"")
                    .addVar("%arena%", arena.getName())
                    .send(player);
        }
        else if (args[1].equalsIgnoreCase("slime")) {
            arena.setSlimeSpawn(Loc.fromLocation(player.getLocation()).shortify());
            SlimeSurvival.getMessages().getArenaSpawnSet()
                    .addVar("%type%", "slime")
                    .addVar("%arena%", arena.getName())
                    .send(sender);
        }
        else {
            SlimeSurvival.getMessages().getInvalidSpawnType().send(sender);
        }
        return true;
    }
}
