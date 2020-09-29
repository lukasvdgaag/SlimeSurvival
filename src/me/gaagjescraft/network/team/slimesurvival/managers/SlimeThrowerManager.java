package me.gaagjescraft.network.team.slimesurvival.managers;

import me.gaagjescraft.network.team.slimesurvival.NMS.v1_12_R1.NMSHandler;
import me.gaagjescraft.network.team.slimesurvival.SlimeSurvival;
import me.gaagjescraft.network.team.slimesurvival.game.SlimePlayer;
import me.gaagjescraft.network.team.slimesurvival.managers.items.ItemsManager;
import me.gaagjescraft.network.team.slimesurvival.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class SlimeThrowerManager {

    private static ArmorStand armorStand = null;

    public static EulerAngle angleToEulerAngle(int degrees){
        return angleToEulerAngle(Math.toRadians(degrees));
    }

    public static EulerAngle angleToEulerAngle(double radians){
        double x = Math.cos(radians);
        double z = Math.sin(radians);
        return new EulerAngle(x,0,z);
    }

    public void spinTick (Entity entity, float toAdd) {
        float endYawTick = entity.getLocation().getYaw() + toAdd;
        if (endYawTick > 360) endYawTick -= 360.0f;
        Location loc = entity.getLocation().clone();
        loc.setYaw(endYawTick);
        entity.teleport(loc);
    }

    public float getClockwiseDifference(float startYaw, float endYaw) {
        if (startYaw < endYaw) {
            return endYaw - startYaw;
        } else {
            return endYaw + 360 - startYaw;
        }
    }

    public void targetSelectorSlimePlayer(ArmorStand as, Player player) {
        Vector v = Loc.fromLocation(player.getLocation()).shortify().getLocation().add(0.5,0,0.5).subtract(as.getEyeLocation()).toVector();
        Location l = as.getLocation().clone().setDirection(v);

        float yaw = l.getYaw(); // the yaw end location it should point to

        final int[] i = {0};

        player.sendMessage("Current yaw: " + as.getLocation().getYaw() + "\n" +
                "Target yaw: " + yaw);

        float floatDifference = getClockwiseDifference(as.getLocation().getYaw(), yaw); // add 9 yaw points per tick (to make it smooth)

        //Bukkit.getLogger().log(Level.WARNING, as.getLocation().getYaw() + "");

        float yawPerTick = 6f;
        float ticksToRun = floatDifference / yawPerTick;
        int finalTicksToRun = (int) ticksToRun;
        float partialTickRemainder = ticksToRun - finalTicksToRun;
        float finalYawPerTick = yawPerTick;

        player.sendMessage(partialTickRemainder + "  " + ticksToRun);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (i[0] >= ticksToRun){
                    player.sendMessage("FINAL POS: " + as.getLocation().getYaw() + "");
                    this.cancel();
                    return;
                } else if (i[0] >= finalTicksToRun) {
                    spinTick(as, finalYawPerTick * partialTickRemainder);
                } else {
                    spinTick(as, finalYawPerTick);
                }
                i[0]++;
            }
        }.runTaskTimer(SlimeSurvival.get(),0,1);
    }

    public void spawnSelectorSlime(Player player) {
//        if (arena.getState() != ArenaState.STARTING) return;
        //Location spawn = arena.getSlimeSpawn().getLocation().add(0.5,-2,0.5);
        Location spawn = Loc.fromLocation(player.getLocation()).shortify().getLocation();
        spawn.add(0.5,-2,0.5);
        spawn.setYaw(0);
        spawn.setPitch(0);
        boolean fromMemory = false;
        ArmorStand as;
        if (armorStand == null) {
            as  = (ArmorStand) spawn.getWorld().spawnEntity(spawn,EntityType.ARMOR_STAND);
            as.setInvulnerable(true);
            as.setVisible(false);
            //as.setAI(false);
            as.setGravity(false);
            as.setMarker(false);
            as.setHelmet(ItemsManager.ARMOR_SLIME_HEAD);
            armorStand = as;
        }
        else {
            as = armorStand;
            fromMemory = true;

        }

        final int[] i = {0};

        boolean finalFromMemory = fromMemory;
        new BukkitRunnable() {
            @Override
            public void run() {
                int ticksToRun = 40;

                if (i[0] >= ticksToRun || finalFromMemory) {
                    targetSelectorSlimePlayer(as, player);
                    player.sendMessage(ChatColor.DARK_GREEN + "Now targeting your location...");
                    this.cancel();
                } else {
                    Location newLoc = as.getLocation();
                    newLoc.add(0,(0.55/40),0);

                    if (newLoc.getYaw() >= 0)
                        newLoc.setYaw(newLoc.getYaw()+9);
                    else
                        newLoc.setY(-newLoc.getYaw()+9);

                    //as.setHeadPose(angleToEulerAngle(deg));


                    as.teleport(newLoc);


                    i[0]++;
                }
            }
        }.runTaskTimer(SlimeSurvival.get(),0,1);
    }

    public void throwSlime(SlimePlayer player) {
        Player p = player.getPlayer();

        Slime slime = (Slime) p.getWorld().spawnEntity(p.getLocation(), EntityType.SLIME);
        NMSHandler.modifyEntity(slime);

        Location loc = p.getLocation();
        Vector v2 = loc.getDirection().multiply(2.35);
        slime.setVelocity(v2);
        slime.setMetadata("slimesurvival", new FixedMetadataValue(SlimeSurvival.get(), p.getName()));


        //as.remove();
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlimeSurvival.get(), slime::remove, 200);
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

    public void throwSlimeball(Player player) {
        Vector target = player.getEyeLocation().toVector();
        Vector current = armorStand.getLocation().toVector();



    }

}
