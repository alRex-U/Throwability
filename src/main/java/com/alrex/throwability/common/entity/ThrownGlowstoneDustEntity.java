package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class ThrownGlowstoneDustEntity extends ThrowableItemProjectile {
    public ThrownGlowstoneDustEntity(EntityType<? extends ThrownGlowstoneDustEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownGlowstoneDustEntity(Level level, LivingEntity thrower) {
        super(EntityTypes.THROWN_GLOWSTONE_DUST.get(), thrower, level);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide) {
            var nearEntities = level().getEntities(this, getBoundingBox().inflate(4));
            for (var entity : nearEntities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
                }
            }
            Direction direction = (hitResult instanceof BlockHitResult blockHitResult)
                    ? blockHitResult.getDirection().getOpposite()
                    : null;
            level().broadcastEntityEvent(this, (byte) (direction == null ? Direction.values().length : direction.ordinal()));
            discard();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID <= Direction.values().length) {
            ParticleUtils.spawnScatteringParticle(ParticleTypes.END_ROD, level(), position(), random, 0.3, 0.08, 12,
                    eventID == Direction.values().length ? null : Direction.values()[eventID]);
            var particleData = ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem());
            for (int i = 0; i < 8; i++) {
                level().addParticle(particleData, getX(), getY(), getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GLOWSTONE_DUST;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
