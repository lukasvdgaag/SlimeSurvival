package me.gaagjescraft.network.team.slimesurvival.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Loc {

    private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public Loc(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Loc(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public static Loc fromLocation(Location loc) {
        return new Loc(loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch());
    }

    public static Loc fromString(String loc) {
        if (loc == null) return null;
        String[] arg0 = loc.split(":");
        if (arg0.length == 4) {
            return new Loc(arg0[0], Double.parseDouble(arg0[1]), Double.parseDouble(arg0[2]), Double.parseDouble(arg0[3]));
        }
        else if (arg0.length == 6) {
            return new Loc(arg0[0], Double.parseDouble(arg0[1]), Double.parseDouble(arg0[2]), Double.parseDouble(arg0[3]), Float.parseFloat(arg0[4]), Float.parseFloat(arg0[5]));
        }
        return null;
    }

    public Location getLocation() {
        World w = Bukkit.getWorld(world);
        if (w != null) return new Location(w, x, y, z, yaw ,pitch);
        return null;
    }

    public Loc shortify() {
        this.x = Math.floor(x);
        this.y = Math.floor(y);
        this.z = Math.floor(z);
        this.yaw = 0;
        this.pitch = 0;
        return this;
    }

    public boolean matches(Loc loc) {
        return (x==loc.getX() && y==loc.getY() && z==loc.getZ() && world.equals(loc.getWorld()));
    }

    @Override
    public String toString() {
        return world + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
