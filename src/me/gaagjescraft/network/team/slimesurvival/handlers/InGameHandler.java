package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.ArenaState;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class InGameHandler implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e ) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer(e.getPlayer());
        if (sp == null) return;

        sp.getArena().leave(sp);
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
                sp.getArena().getVoteModeMenu().update(sp);
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
    public void onInventoryClose(InventoryCloseEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer((Player) e.getPlayer());
        if (sp == null) return;

        if (sp.getArena().getVoteModeMenu().getViewers().contains(sp)) {
            sp.getArena().getVoteModeMenu().removeViewer(sp);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        SlimePlayer sp = SlimeSurvival.getSlimePlayer((Player) e.getWhoClicked());
        if (sp == null) return;

        e.setCancelled(true);

        if (e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getBottomInventory())) return;

        if (sp.getArena().getVoteModeMenu().getViewers().contains(sp)) {
            sp.getArena().getVoteModeMenu().click(sp, e.getRawSlot());
        }
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


}
