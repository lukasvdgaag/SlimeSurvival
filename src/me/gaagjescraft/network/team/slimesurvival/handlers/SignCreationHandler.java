package me.gaagjescraft.network.team.slimesurvival.handlers;

import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import me.gaagjescraft.network.team.slimesurvival.utils.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SignCreationHandler implements Listener {

    private static ArmorStand armorStand = null;
    private static HashMap<Player, BukkitTask> runningThrowers = new HashMap<>();
    private static HashMap<Player, Integer> throwTimers = new HashMap<>();

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
            Bukkit.getScheduler().runTaskLater(SlimeSurvival.get(), () -> {
                arena.getSignManager().update(loc);
            }, 20);
        }
    }

    @EventHandler
    public void onSignInteraction(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || !e.getClickedBlock().getWorld().getName().equals(SlimeSurvival.getCfg().getLobbySpawn().getWorld()))
            return;

        Loc loc = Loc.fromLocation(e.getClickedBlock().getLocation());
        for (SlimeArena arena : SlimeSurvival.get().getArenas()) {
            if (arena.hasSign(loc)) {
                arena.addPlayer(e.getPlayer());
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!e.getMessage().startsWith("throwSlime")) {
            return;
        }

        double multiply = 1.00;
        int size = 1;
        String[] args = e.getMessage().split(" ");
        if (args.length >= 2 && SlimeUtils.isDouble(args[1])) {
            multiply = Double.parseDouble(args[1]);
        }
        if (args.length >= 3 && SlimeUtils.isInt(args[2])) {
            size = Integer.parseInt(args[2]);
        }

        double finalMultiply = multiply;
        int finalSize = size;
        Bukkit.getScheduler().runTask(SlimeSurvival.get(), () -> {
            Slime slime = (Slime) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getEyeLocation(), EntityType.SLIME);
            SlimeSurvival.getNMS().modifyEntity(slime);

            Location loc = e.getPlayer().getEyeLocation();
            Vector v2 = loc.getDirection().multiply(finalMultiply);
            slime.setSize(finalSize);
            slime.setVelocity(v2);
            slime.setMetadata("slimesurvival", new FixedMetadataValue(SlimeSurvival.get(), e.getPlayer().getName()));
        });

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Material.STICK) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        throwTimers.put(e.getPlayer(), throwTimers.getOrDefault(e.getPlayer(), 0) + 1);

        if (!runningThrowers.containsKey(e.getPlayer())) {
            BukkitTask run = new BukkitRunnable() {
                int oldValue = 0;
                int timer = 0;

                @Override
                public void run() {
                    int newValue = throwTimers.getOrDefault(e.getPlayer(), 0);
                    if (newValue <= oldValue) {
                        this.cancel();
                        e.getPlayer().removePotionEffect(PotionEffectType.SLOW);

                        Slime slime = (Slime) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getEyeLocation(), EntityType.SLIME);
                        SlimeSurvival.getNMS().modifyEntity(slime);

                        Location loc = e.getPlayer().getEyeLocation();
                        Vector v2 = loc.getDirection().multiply(1 + (0.3 * timer));
                        slime.setSize(1+(int) timer / 2);
                        slime.setVelocity(v2);
                        slime.setMetadata("slimesurvival", new FixedMetadataValue(SlimeSurvival.get(), e.getPlayer().getName()));

                        throwTimers.remove(e.getPlayer());
                        runningThrowers.remove(e.getPlayer());
                        return;
                    }


                    oldValue = newValue;
                    if (timer < 10) {
                        timer++;
                    }

                    String red = ChatColor.RED + "|";
                    String green = ChatColor.GREEN + "|";

                    StringBuilder result = new StringBuilder();
                    for (int i = 0;i<timer;i++) {
                        result.append(green);
                    }
                    for (int i = timer; i<10; i++) {
                        result.append(red);
                    }


                    e.getPlayer().sendTitle(result.toString(),"",0,5,0);
                    e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, timer));
                }
            }.runTaskTimer(SlimeSurvival.get(), 0, 4);
            runningThrowers.put(e.getPlayer(), run);
        }

    }


}
