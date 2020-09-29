package me.gaagjescraft.network.team.slimesurvival.handlers;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.List;

public class InGameHandler implements Listener {

    private static List<Player> slimeThrowCooldowns = Lists.newArrayList();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().equals("placeMovingSlime")) {
            Bukkit.getScheduler().runTask(SlimeSurvival.get(), () -> {
                SlimeSurvival.getSTM().spawnSelectorSlime(e.getPlayer());
            });
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        e.setCancelled(true);
        if (!e.hasItem()) return;

        if (sp.getArena().getState() == ArenaState.WAITING) {
            if (e.getItem().equals(ItemsManager.ITEM_LEAVE)) {
                sp.getArena().leave(sp);
            }
            if (e.getItem().equals(ItemsManager.ITEM_VOTE_MODE)) {
                // open vote menu here
                SlimeSurvival.getSTM().throwSlime(sp); // todo remove this
            }
        }

        if (sp.getArena().getState() == ArenaState.PLAYING) {
            if (sp.getTeam() == TeamType.SLIME) {
                if (e.getItem().equals(ItemsManager.ITEM_SLIME_THROWER)) {
                    SlimeSurvival.getSTM().throwSlime(sp);
                }
                if (e.getItem().equals(ItemsManager.ITEM_SLIME_BOMBER)) {
                    // bomb a slime
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer((Player) e.getWhoClicked());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        SlimePlayer sp = SlimeSurvival.getSlimePlayer((Player) e.getEntity());
        if (sp == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer((Player) e.getEntity());
        if (sp == null) return;

        e.setFoodLevel(20);
    }

    @EventHandler
    public void onSlimeCollision(PlayerMoveEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        //if (sp.getArena().getState() == ArenaState.PLAYING && sp.getTeam() == TeamType.SURVIVOR) {
        // player is survivor
        if (sp.getTeam() == TeamType.SLIME) return;

        for (Entity entity : e.getPlayer().getNearbyEntities(0.5, 0.5, 0.5)) {
            SlimePlayer owner = SlimeSurvival.getSTM().getSlimeOwner(entity);
            if (owner != null /*&& owner.getTeam() == TeamType.SLIME*/) {
                entity.remove();
                owner.addKill();
                // todo add mode check
                sp.getArena().prepareForTeam(sp, TeamType.SLIME);
                e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You are now a slime!");
            }
        }

        //}
    }


}
