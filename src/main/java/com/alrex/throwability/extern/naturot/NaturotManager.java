package com.alrex.throwability.extern.naturot;

import com.alrex.naturot.NaturotUtils;
import com.alrex.throwability.extern.ExternalModManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class NaturotManager extends ExternalModManager {
    public NaturotManager() {
        super("naturot");
    }

    public void rotateEntity(Entity entity, Vec3 deltaMovement) {
        if (!isInstalled()) return;
        var rotAxis = deltaMovement.normalize().cross(new Vec3(0, 1, 0));
        NaturotUtils.setRotation(
                entity,
                rotAxis,
                (float) (Mth.clamp(deltaMovement.length() * (0.7 + 0.6 * entity.level.getRandom().nextDouble()), 0, 1) * Math.PI / 4.)
        );
    }
}
