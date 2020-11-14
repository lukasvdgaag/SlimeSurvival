package me.gaagjescraft.network.team.slimesurvival.NMS;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Slime;

public abstract interface NMS {

    public abstract void modifyEntity(Slime e);

    public abstract void modifySelectorSlime(ArmorStand as);

    public abstract int getVersion();

}
