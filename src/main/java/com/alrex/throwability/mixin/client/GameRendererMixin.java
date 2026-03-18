package com.alrex.throwability.mixin.client;

import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
    private EntityHitResult onGetEntityHitResult(Entity cameraEntity, Vec3 eyePosition, Vec3 viewVector, AABB availableArea, Predicate<Entity> entityPredicate, double pickRange) {
        if (cameraEntity instanceof LocalPlayer localPlayer) {
            var mainHandItem = (localPlayer).getItemInHand(InteractionHand.MAIN_HAND);
            if (mainHandItem.isEmpty()) {
                return ProjectileUtil.getEntityHitResult(cameraEntity, eyePosition, viewVector, availableArea,
                        (entity) -> entityPredicate.test(entity)
                                || entity instanceof ItemEntity
                                || entity instanceof ThrownWeaponEntity,
                        pickRange
                );
            }
        }
        return ProjectileUtil.getEntityHitResult(cameraEntity, eyePosition, viewVector, availableArea, entityPredicate, pickRange);
    }
}
