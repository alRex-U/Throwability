package com.alrex.throwability.extern.naturot;

import com.alrex.naturot.util.EntityUtils;
import com.alrex.throwability.extern.ExternalModManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class NaturotManager extends ExternalModManager {
    public NaturotManager() {
        super("naturot");
    }

    public void rotateEntity(Entity entity, Vector3d deltaMovement) {
        if (isInstalled()) {
            EntityUtils.applyBasicRotation(entity, deltaMovement);
        }
    }
}
