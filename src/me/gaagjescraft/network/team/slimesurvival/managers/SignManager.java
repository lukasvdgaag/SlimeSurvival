package me.gaagjescraft.network.team.slimesurvival.managers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SignManager {

    private List<Loc> signLocations;
    private SlimeArena arena;

    public SignManager(SlimeArena arena) {
        this.arena = arena;
        this.signLocations = arena.getJoinSigns();
    }

    public void updateAll() {
        for (Loc a : signLocations) {
            update(a);
        }
    }

    public void update(Loc loc) {
        Block signBlock = loc.getLocation().getBlock();
        if (signBlock.getState() instanceof Sign) {
            Sign sign = (Sign) signBlock.getState();

            List<String> lines = SlimeSurvival.getMessages().getSignFormat(arena.getState().name().toLowerCase())
                    .addVar("%arena%", arena.getDisplayName())
                    .addVar("%players%", arena.getGamePlayers().size()+"")
                    .addVar("%maxPlayers%", arena.getWaitingSpawns().size()+"")
                    .addVar("%state%", arena.getState().name())
                    .addVar("%mode%", arena.getMode().name()).getMessage();

            for (int i=0;i<4;i++) {
                sign.setLine(i, i<lines.size() ? lines.get(i) : "");
            }
            sign.update();

            Block attached = signBlock.getRelative(((org.bukkit.material.Sign)sign.getData()).getAttachedFace());
            String blockFromConfig = "STONE";
            if (arena.getState() == ArenaState.DISABLED) {
                blockFromConfig = SlimeSurvival.getCfg().getSignDisabledBlock();
            }
            else if (arena.getState() == ArenaState.PLAYING) {
                blockFromConfig = SlimeSurvival.getCfg().getSignPlayingBlock();
            }
            else if (arena.getState() == ArenaState.WAITING) {
                blockFromConfig = SlimeSurvival.getCfg().getSignWaitingBlock();
            }
            else if (arena.getState() == ArenaState.STARTING) {
                blockFromConfig = SlimeSurvival.getCfg().getSignStartingBlock();
            }
            else if (arena.getState() == ArenaState.ENDING) {
                blockFromConfig = SlimeSurvival.getCfg().getSignEndingBlock();
            }
            ItemStack i = SlimeUtils.getItemStackFromString(blockFromConfig);

            attached.setType(i.getType());
            attached.setData(i.getData().getData());
        }
    }


}
