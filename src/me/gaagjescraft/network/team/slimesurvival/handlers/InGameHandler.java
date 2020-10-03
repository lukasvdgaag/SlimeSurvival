package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaMode;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class InGameHandler implements Listener {

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
            }
        }

        if (sp.getArena().getState() == ArenaState.PLAYING) {
            if (sp.getTeam() == TeamType.SLIME && sp.getArena().isSlimesReleased()) {
                if (e.getItem().equals(ItemsManager.ITEM_SLIME_THROWER)) {
                    sp.getArena().getSlimeThrowerManager().throwSlime(sp);
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
        if (sp == null || sp.getArena().getState() != ArenaState.PLAYING) return;


        for (Entity entity : e.getPlayer().getNearbyEntities(0.5, 0.5, 0.5)) {
            SlimePlayer owner = sp.getArena().getSlimeThrowerManager().getSlimeOwner(entity);
            if (owner != null) {

                if (owner != sp) {
                    if (sp.getTeam() == TeamType.SURVIVOR && sp.getArena().getMode() == ArenaMode.NORMAL) {
                        entity.remove();
                        owner.addKill();
                        // todo add mode check
                        sp.getArena().prepareForTeam(sp, TeamType.SLIME);
                        e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "You are now a slime!");
                        owner.getArena().giveItem(owner, 0, ItemsManager.ITEM_SLIME_THROWER);
                    }
                }
                else if (entity.getTicksLived()>5){
                    // player is the slime owner
                    entity.remove();
                    sp.getArena().giveItem(sp, 0, ItemsManager.ITEM_SLIME_THROWER);
                }
            }
        }

        //}
    }


}
