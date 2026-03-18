package com.alrex.throwability.common.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownGunPowderEntity extends ThrowableItemProjectile {
    public ThrownGunPowderEntity(EntityType<? extends ThrownGunPowderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownGunPowderEntity(Level level, LivingEntity entity) {
        super(EntityTypes.THROWN_GUNPOWDER.get(), entity, level);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!level.isClientSide) {
            var pos = position();
            level.explode(this, pos.x, pos.y, pos.z, 0.8f, Explosion.BlockInteraction.NONE);
            discard();
        }

    }

    @Override
    protected Item getDefaultItem() {
        return Items.GUNPOWDER;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
