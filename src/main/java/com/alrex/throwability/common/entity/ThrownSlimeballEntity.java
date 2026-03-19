package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownSlimeballEntity extends ThrowableItemProjectile {
    private int boundingCount = 0;

    public ThrownSlimeballEntity(EntityType<? extends ThrownSlimeballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownSlimeballEntity(Level level, LivingEntity entity) {
        super(EntityTypes.THROWN_SLIMEBALL.get(), entity, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (this.tickCount % 4 == 0) {
                ParticleUtils.spawnScatteringParticle(ParticleTypes.ITEM_SLIME, level(), position(), random, 0.1, 0.04, 1, getDeltaMovement());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        boundingCount++;
        if (boundingCount > 3) {
            if (!level().isClientSide) {
                level().broadcastEntityEvent(this, (byte) 3);
                discard();
            }
        } else {
            var normal = VectorUtil.create3dFrom3i(blockHitResult.getDirection().getNormal());
            setDeltaMovement(VectorUtil.reflect(getDeltaMovement(), normal).scale(0.9));
        }
    }

    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            var particleData = ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem());
            for (int i = 0; i < 8; i++) {
                level().addParticle(particleData, getX(), getY(), getZ(), 0, 0, 0);
            }
        } else if (eventID == 4) {
            ParticleUtils.spawnScatteringParticle(
                    ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem()),
                    level(), position(), random, 0.2, 0.08, 16
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        var entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4));
            level().broadcastEntityEvent(this, (byte) 4);
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Bound", boundingCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        boundingCount = tag.getInt("Bound");
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
