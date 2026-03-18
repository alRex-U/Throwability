package com.alrex.throwability.client.render.entity;

import com.alrex.throwability.common.entity.EntityTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import static net.minecraft.client.renderer.entity.EntityRenderers.register;

public class EntityRenderers {
    public static void registerRenderers() {
        register(EntityTypes.THROWN_WEAPON.get(), ThrownWeaponRenderer::new);
        register(EntityTypes.THROWN_SPAWN_EGG.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_GLOWSTONE_DUST.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_BLAZE_POWDER.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_BLAZE_ROD.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_GHAST_TEAR.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_GUNPOWDER.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_SLIMEBALL.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_INK_SAC.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_GLOW_INK_SAC.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_BUCKET.get(), ThrownItemRenderer::new);
        register(EntityTypes.THROWN_SPIDER_EYE.get(), ThrownItemRenderer::new);
    }
}
