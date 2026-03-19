package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class ThrownBlazeRodEntity extends ThrowableItemProjectile {

    public ThrownBlazeRodEntity(EntityType<? extends ThrownBlazeRodEntity> type, Level level) {
        super(type, level);
    }

    public ThrownBlazeRodEntity(Level level, LivingEntity thrower) {
        super(EntityTypes.THROWN_BLAZE_ROD.get(), thrower, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (this.tickCount % 2 == 0) {
                ParticleUtils.spawnScatteringParticle(ParticleTypes.FLAME, level(), position(), random, 0.3, 0.08, 1, getDeltaMovement());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!level().isClientSide) {
            int maxXOffset = 2, maxYOffset = 2, maxZOffset = 2;
            switch (blockHitResult.getDirection().getAxis()) {
                case X:
                    maxXOffset = 1;
                    break;
                case Y:
                    maxYOffset = 1;
                    break;
                case Z:
                    maxZOffset = 1;
                    break;
            }
            level().explode(this, getX(), getY(), getZ(), 1f, Level.ExplosionInteraction.TNT);
            for (int yOffset = -maxYOffset; yOffset <= maxYOffset; yOffset++) {
                for (int xOffset = -maxXOffset; xOffset <= maxXOffset; xOffset++) {
                    for (int zOffset = -maxZOffset; zOffset <= maxZOffset; zOffset++) {
                        if (random.nextDouble() > 0.65) continue;
                        var pos = blockPosition().offset(xOffset, yOffset, zOffset);
                        if (!level().isLoaded(pos)) continue;
                        if (FireBlock.canBePlacedAt(level(), pos, blockHitResult.getDirection())) {
                            level().setBlockAndUpdate(pos, FireBlock.getState(level(), pos));
                        }
                    }
                }
            }
            level().broadcastEntityEvent(this, (byte) 3);
            discard();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            ParticleUtils.spawnScatteringParticle(ParticleTypes.FLAME, level(), position(), random, 0.3, 0.08, 24);
            ParticleUtils.spawnScatteringParticle(ParticleTypes.LARGE_SMOKE, level(), position(), random, 0.22, 0.08, 12);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BLAZE_ROD;
    }
}
