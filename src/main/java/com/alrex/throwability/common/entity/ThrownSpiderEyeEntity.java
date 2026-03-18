package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class ThrownSpiderEyeEntity extends ThrowableItemProjectile {
    public ThrownSpiderEyeEntity(EntityType<? extends ThrownSpiderEyeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownSpiderEyeEntity(Level level, LivingEntity entity) {
        super(EntityTypes.THROWN_SPIDER_EYE.get(), entity, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!level.isClientSide) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 30, 2));
            }
            level.broadcastEntityEvent(this, (byte) 4);
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            var particleData = ParticleUtils.getItemParticle(null, getItem());
            for (int i = 0; i < 8; i++) {
                level.addParticle(particleData, getX(), getY(), getZ(), 0, 0, 0);
            }
        } else if (eventID == 4) {
            ParticleUtils.spawnScatteringParticle(
                    ParticleUtils.getItemParticle(null, getItem()),
                    level, position(), random, 0.2, 0.08, 16
            );
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPIDER_EYE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
