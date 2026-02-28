package com.alrex.throwability.client.render.entity;

import com.alrex.throwability.common.entity.EntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;

public class EntityRenderers {
    public static void register(EntityRendererManager manager) {
        manager.register(EntityTypes.THROWN_WEAPON.get(), new ThrownWeaponRenderer(manager));
        manager.register(EntityTypes.THROWN_SPAWN_EGG.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_GLOWSTONE_DUST.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
