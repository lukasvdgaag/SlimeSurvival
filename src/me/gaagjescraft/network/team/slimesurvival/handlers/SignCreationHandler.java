package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SignCreationHandler implements Listener {

    private static ArmorStand armorStand = null;

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        if (!e.getBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) return;

        Loc loc = Loc.fromLocation(e.getBlock().getLocation());
        for (SlimeArena arena : SlimeSurvival.get().getArenas()) {
            if (arena.hasSign(loc)) {
                arena.removeSign(loc);
                arena.saveArenaData();
                SlimeSurvival.getMessages().getSignRemoved().addVar("%arena%", arena.getDisplayName()).send(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onSignCreation(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[ss]") ||
                e.getLine(0).equalsIgnoreCase("[slimesurvival]")) {
            if (!e.getBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) {
                SlimeSurvival.getMessages().getMustBeInLobby().send(e.getPlayer());
                return;
            }

            SlimeArena arena = SlimeSurvival.getArena(e.getLine(1));
            if (arena == null) {
                //invalid arena
                SlimeSurvival.getMessages().getArenaNotExisting().send(e.getPlayer());
                return;
            }

            Loc loc = Loc.fromLocation(e.getBlock().getLocation());
            if (!arena.hasSign(loc)) {
                arena.addSign(loc);
                arena.saveArenaData();
            }
            Bukkit.getScheduler().runTaskLater(SlimeSurvival.get(), ()-> {
                arena.getSignManager().update(loc);
            },20);
        }
    }

    @EventHandler
    public void onSignInteraction(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || !e.getClickedBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld())) return;

        Loc loc = Loc.fromLocation(e.getClickedBlock().getLocation());
        for (SlimeArena arena : SlimeSurvival.get().getArenas()) {
            if (arena.hasSign(loc)) {
                arena.addPlayer(e.getPlayer());
                return;
            }
        }
    }

    public void bobSlimeHead(Player slimePlayer) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;

                if (i<=7) {
                    armorStand.getHeadPose().add(0,0,4);
                    armorStand.getEyeLocation().setYaw(armorStand.getEyeLocation().getYaw()+4);
                    //armorStand.setHeadPose(armorStand.getHeadPose().setZ(armorStand.getHeadPose().getZ()+4));
                }
                else if (i<=14) {
                    armorStand.getEyeLocation().setYaw(armorStand.getEyeLocation().getYaw()-4);
                    //armorStand.setHeadPose(armorStand.getHeadPose().setZ(armorStand.getHeadPose().getZ()-4));
                }
                else if (i<=21) {
                    if (i == 21) {
                        armorStand.getEyeLocation().setYaw(360-4);
                        armorStand.setHeadPose(armorStand.getHeadPose().setZ(360 - 4));
                    }
                    else {
                        armorStand.getEyeLocation().setYaw(armorStand.getEyeLocation().getYaw()-4);
                        //armorStand.setHeadPose(armorStand.getHeadPose().setZ(armorStand.getHeadPose().getZ()-4));
                    }
                }
                else if (i<=29) {
                    armorStand.getEyeLocation().setYaw(armorStand.getEyeLocation().getYaw()+4);
                    //armorStand.setHeadPose(armorStand.getHeadPose().setZ(armorStand.getHeadPose().getZ()+4));
                    armorStand.teleport(armorStand.getLocation().add(0,0.1,0));

                    if (i == 29) {
                        Vector target = slimePlayer.getPlayer().getEyeLocation().add(0,-0.5,0).toVector();
                        Vector current = armorStand.getLocation().toVector();

                        Vector between = target.subtract(current);
                        armorStand.setVelocity(between.normalize());
                    }
                }
                else {
                    if (armorStand.getNearbyEntities(0.1,0.1,0.1).contains(slimePlayer.getPlayer())) {
                        armorStand.remove();
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(SlimeSurvival.get(),0,1);
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().startsWith("slime:abc")) {

            Bukkit.getScheduler().runTask(SlimeSurvival.get(), () -> {
                ArmorStand armorStand = ((ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getEyeLocation(), EntityType.ARMOR_STAND));
                armorStand.setSmall(true);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(true);
                armorStand.setVisible(false);
                armorStand.setMarker(false);
                armorStand.setHelmet(ItemsManager.ARMOR_SLIME_HEAD);
                SlimeSurvival.getNMS().modifySelectorSlime(armorStand);
                Location loc = e.getPlayer().getEyeLocation();
                Vector v2 = loc.getDirection().multiply(2.75);
                armorStand.setVelocity(v2);
            });
        }
    }

}
