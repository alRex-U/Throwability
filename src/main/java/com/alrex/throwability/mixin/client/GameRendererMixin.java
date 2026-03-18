package com.alrex.throwability.mixin.client;

import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IResourceManagerReloadListener, AutoCloseable {
    @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileHelper;getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"))
    private EntityRayTraceResult onGetEntityHitResult(Entity cameraEntity, Vector3d eyePosition, Vector3d viewVector, AxisAlignedBB availableArea, Predicate<Entity> entityPredicate, double pickRange) {
        if (cameraEntity instanceof ClientPlayerEntity) {
            ItemStack mainHandItem = ((ClientPlayerEntity) cameraEntity).getItemInHand(Hand.MAIN_HAND);
            if (mainHandItem.isEmpty()) {
                return ProjectileHelper.getEntityHitResult(cameraEntity, eyePosition, viewVector, availableArea,
                        (entity) -> entityPredicate.test(entity)
                                || entity instanceof ItemEntity
                                || entity instanceof ThrownWeaponEntity,
                        pickRange
                );
            }
        }
        return ProjectileHelper.getEntityHitResult(cameraEntity, eyePosition, viewVector, availableArea, entityPredicate, pickRange);
    }
}
