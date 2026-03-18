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
        manager.register(EntityTypes.THROWN_BLAZE_POWDER.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_BLAZE_ROD.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_GHAST_TEAR.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_GUNPOWDER.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_SLIMEBALL.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_INK_SAC.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_BUCKET.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        manager.register(EntityTypes.THROWN_SPIDER_EYE.get(), new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
