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
        entity.getLocation().setYaw(endYawTick);
    }

    public float getClockwiseDifference(float startYaw, float endYaw) {
        float toSpinDeg = 0;
        if (startYaw < endYaw) {
            toSpinDeg = endYaw - startYaw;
        } else {
            toSpinDeg = endYaw + 360 - startYaw;
        }
        return toSpinDeg;
    }

    public void targetSelectorSlimePlayer(ArmorStand as, Player player) {
        Vector v = Loc.fromLocation(player.getLocation()).shortify().getLocation().add(0.5,1,0.5).subtract(as.getEyeLocation()).toVector();
        Location l = as.getLocation().clone().setDirection(v);

        float yaw = l.getYaw(); // the yaw end location it should point to

        final int[] i = {0};

        player.sendMessage("Current yaw: " + as.getLocation().getYaw() + "\n" +
                "Target yaw: " + yaw);

        float floatDifference = getClockwiseDifference(as.getLocation().getYaw(),yaw); // add 9 yaw points per tick (to make it smooth)
        int ticksToRun = 60;
        float yawPerTick = floatDifference / ticksToRun;
        //ticksToRun+=40; // add a standard two rounds to the spin
        //float yawPerTick = targetFloat / ticksToRun;


        int finalTicksToRun = ticksToRun;
        new BukkitRunnable() {
            @Override
            public void run() {
                //as.getLocation().getYaw()-targetFloat;

                if (i[0] >= finalTicksToRun) {
                    this.cancel();
                }

               /* Location newLoc = as.getLocation();
                newLoc.setYaw(newLoc.getYaw()+yawPerTick);*/

                spinTick(as, yawPerTick);

                //as.setHeadPose(angleToEulerAngle(deg));
               // as.teleport(newLoc);
                i[0]++;
            }
        }.runTaskTimer(SlimeSurvival.get(),0,1);
    }

    public void spawnSelectorSlime(Player player) {
//        if (arena.getState() != ArenaState.STARTING) return;
        //Location spawn = arena.getSlimeSpawn().getLocation().add(0.5,-2,0.5);
        Location spawn = player.getLocation().add(0,-2,0);
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
                }

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


}
