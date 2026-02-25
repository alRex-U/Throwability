package com.alrex.throwability.client.render.entity;

import com.alrex.throwability.common.entity.EntityTypes;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class EntityRenderers {
    public static void register(EntityRendererManager manager) {
        manager.register(EntityTypes.THROWN_WEAPON.get(), new ThrownWeaponRenderer(manager));
    }
}
