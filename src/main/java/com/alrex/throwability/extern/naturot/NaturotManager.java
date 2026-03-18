package com.alrex.throwability.extern.naturot;

import com.alrex.naturot.NaturotUtils;
import com.alrex.throwability.extern.ExternalModManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class NaturotManager extends ExternalModManager {
    public NaturotManager() {
        super("naturot");
    }

    public void rotateEntity(Entity entity, Vector3d deltaMovement) {
        if (!isInstalled()) return;
        Vector3d rotAxis = deltaMovement.normalize().cross(new Vector3d(0, 1, 0));
        NaturotUtils.setRotation(
                entity,
                rotAxis,
                (float) (MathHelper.clamp(deltaMovement.length() * (0.7 + 0.6 * entity.level.getRandom().nextDouble()), 0, 1) * Math.PI / 4.)
        );
    }
}
