package me.gaagjescraft.network.team.slimesurvival.managers;

import me.gaagjescraft.network.team.slimesurvival.NMS.v1_12_R1.NMSHandler;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.enums.TeamType;
import me.gaagjescraft.network.team.slimesurvival.game.SlimeArena;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class SlimeThrowerManager {

    private ArmorStand armorStand;
    private SlimeArena arena;

    public SlimeThrowerManager(SlimeArena arena) {
        this.arena = arena;
    }

    private void spinTick(Entity entity, float toAdd) {
        float endYawTick = entity.getLocation().getYaw() + toAdd;
        Location loc = entity.getLocation().clone();
        loc.setYaw(endYawTick % 360);
        entity.teleport(loc);
    }

    private float getClockwiseDifference(float startYaw, float endYaw) {
        float toSpinDeg = 0;
        if (startYaw < endYaw) {
            toSpinDeg = endYaw - startYaw;
        } else {
            toSpinDeg = endYaw + 360 - startYaw;
        }
        return toSpinDeg;
    }

    public void targetSelectorSlimePlayer(SlimePlayer slimePlayer) {
        Player player = slimePlayer.getPlayer();
        Vector v = Loc.fromLocation(player.getLocation()).shortify().getLocation()/*.add(0.5, 0, 0.5)*/.subtract(armorStand.getEyeLocation()).toVector();
        Location l = armorStand.getLocation().clone().setDirection(v);

        float yaw = l.getYaw(); // the yaw end location it should point to

        final int[] i = {0};

        float floatDifference = getClockwiseDifference(armorStand.getLocation().getYaw(), yaw); // add 9 yaw points per tick (to make it smooth)


        int floatD = ((int) (floatDifference / 5) * 5);
        int ticksToRun = (int) (floatD / 4.5);
        float yawPerTick = 4.5f;
        int finalTicksToRun = ticksToRun;
        float finalYawPerTick = yawPerTick;

        new BukkitRunnable() {
            @Override
            public void run() {
                i[0]++;
                if (i[0] >= finalTicksToRun - 1 || armorStand.getLocation().getYaw() == yaw) {
                    this.cancel();
                    throwSlimeball(slimePlayer);
                    return;
                }

                spinTick(armorStand, finalYawPerTick);
            }
        }.runTaskTimer(SlimeSurvival.get(), 0, 1);
    }

    public void removeSelectorSlime() {
        if (armorStand != null) this.armorStand.remove();
    }

    public void spawnSelectorSlime(SlimePlayer firstTargetPlayer) {
        Location spawn = arena.getSlimeSpawn().getLocation();
        spawn.add(0,-2,0);
        spawn.setYaw(0);
        spawn.setPitch(0);
        boolean fromMemory = false;
        ArmorStand as;
        if (armorStand == null) {
            as = (ArmorStand) spawn.getWorld().spawnEntity(spawn, EntityType.ARMOR_STAND);
            as.setInvulnerable(true);
            as.setVisible(false);
            as.setGravity(false);
            as.setMarker(false);
            as.setHelmet(ItemsManager.ARMOR_SLIME_HEAD);
            as.setMetadata("slimesurvival", new FixedMetadataValue(SlimeSurvival.get(), arena.getName()));
            armorStand = as;
        } else {
            as = armorStand;
            fromMemory = true;
        }

        final int[] i = {0};

        boolean finalFromMemory = fromMemory;
        new BukkitRunnable() {
            @Override
            public void run() {
                int ticksToRun = 100;

                if (i[0] >= ticksToRun || finalFromMemory) {
                    targetSelectorSlimePlayer(firstTargetPlayer);
                    this.cancel();
                }

                spinTick(as, 9);

                Location newLoc = as.getLocation();
                if (i[0] <= 40) {
                    newLoc.add(0, (0.55 / 40), 0);
                }
                as.teleport(newLoc);
                i[0]++;
            }
        }.runTaskTimer(SlimeSurvival.get(), 0, 1);
    }

    public void throwSlime(SlimePlayer player) {
        Player p = player.getPlayer();
        player.getArena().removeItem(player,0); // todo make slot configurable

        Slime slime = (Slime) p.getWorld().spawnEntity(p.getLocation(), EntityType.SLIME);
        NMSHandler.modifyEntity(slime);

        Location loc = p.getEyeLocation();
        Vector v2 = loc.getDirection().multiply(2.75);
        slime.setVelocity(v2);
        slime.setMetadata("slimesurvival", new FixedMetadataValue(SlimeSurvival.get(), p.getName()));

        //as.remove();
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimeSurvival.get(), () -> {
            if (!slime.isDead()) {
                player.getArena().giveItem(player, 0, ItemsManager.ITEM_SLIME_THROWER);
                slime.remove();
            }
        }, 200);
    }

    public SlimePlayer getSlimeOwner(Entity e) {
        if (e == null) return null;

        if (e.hasMetadata("slimesurvival")) {
            String playerName = e.getMetadata("slimesurvival").get(0).asString().replace("slimesurvival:", "");
            Player p = Bukkit.getPlayer(playerName);
            if (p == null) return null;
            return SlimeSurvival.getSlimePlayer(p);
        }
        return null;
    }

    private void throwSlimeball(SlimePlayer slimePlayer) {
        Player player = slimePlayer.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimeSurvival.get(), () -> {
            Vector target = player.getEyeLocation().add(0,-0.5,0).toVector();
            Vector current = armorStand.getLocation().toVector();

            Vector between = target.subtract(current);

            Item as = (Item) player.getWorld().spawnEntity(armorStand.getLocation().add(0, 2, 0), EntityType.DROPPED_ITEM);
            as.setItemStack(new ItemStack(Material.SLIME_BALL));
            as.setPickupDelay(100);
            as.setInvulnerable(true);

            as.setVelocity(between.normalize());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (as.getNearbyEntities(1.5, 1.5, 1.5).contains(player)) {
                        as.remove();
                        this.cancel();
                        arena.prepareForTeam(slimePlayer, TeamType.SLIME);

                        Bukkit.getScheduler().runTask(SlimeSurvival.get(), ()-> {
                            if (arena.getGamePlayers(TeamType.SLIME).size() < arena.getCalculatedSlimeTeamPlayers()) {
                                List<SlimePlayer> sps = arena.getGamePlayers(null);
                                SlimePlayer nextTarget = arena.getGamePlayers(null).get(new Random().nextInt(sps.size()));
                                targetSelectorSlimePlayer(nextTarget);
                            }
                            else {
                                arena.startMatch();
                            }
                        });
                    }
                }
            }.runTaskTimerAsynchronously(SlimeSurvival.get(), 0, 1);
        }, 20);

    }

}
