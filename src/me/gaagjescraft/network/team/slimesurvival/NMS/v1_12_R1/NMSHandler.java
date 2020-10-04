package me.gaagjescraft.network.team.slimesurvival.NMS.v1_12_R1;

import me.gaagjescraft.network.team.slimesurvival.NMS.NMS;
import net.minecraft.server.v1_12_R1.EntitySlime;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import net.minecraft.server.v1_12_R1.MethodProfiler;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftSlime;
import org.bukkit.entity.Slime;

public class NMSHandler implements NMS {

    public void modifyEntity(Slime e) {
        CraftSlime entity = (CraftSlime) e;
        entity.setInvulnerable(true);
        entity.setSize(3);
        //entity.setAI(false);
        EntitySlime slime = entity.getHandle();
        slime.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        slime.bf = 0;
        entity.setCollidable(false);
        slime.setCustomNameVisible(false);
        slime.goalSelector = new PathfinderGoalSelector(((CraftWorld)e.getWorld()).getHandle().methodProfiler);
        slime.targetSelector = new PathfinderGoalSelector(((CraftWorld)e.getWorld()).getHandle().methodProfiler);
        slime.setHeadRotation(180);

        slime.getNavigation().a(0);
        slime.targetSelector = new PathfinderGoalSelector(new MethodProfiler());
    }

    @Override
    public int getVersion() {
        return 12;
    }
}
