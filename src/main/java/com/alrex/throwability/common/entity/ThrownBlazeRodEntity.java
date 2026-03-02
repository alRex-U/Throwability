package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownBlazeRodEntity extends ProjectileItemEntity implements IRendersAsItem {

    public ThrownBlazeRodEntity(EntityType<? extends ThrownBlazeRodEntity> type, World level) {
        super(type, level);
    }

    public ThrownBlazeRodEntity(World level, LivingEntity thrower) {
        super(EntityTypes.THROWN_BLAZE_ROD.get(), thrower, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            if (this.tickCount % 2 == 0) {
                ParticleUtils.spawnScatteringParticle(ParticleTypes.FLAME, level, position(), random, 0.3, 0.08, 1, getDeltaMovement());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) {
        super.onHitBlock(blockRayTraceResult);
        if (!level.isClientSide) {
            int maxXOffset = 2, maxYOffset = 2, maxZOffset = 2;
            switch (blockRayTraceResult.getDirection().getAxis()) {
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
            level.explode(this, getX(), getY(), getZ(), 1f, Explosion.Mode.BREAK);
            for (int yOffset = -maxYOffset; yOffset <= maxYOffset; yOffset++) {
                for (int xOffset = -maxXOffset; xOffset <= maxXOffset; xOffset++) {
                    for (int zOffset = -maxZOffset; zOffset <= maxZOffset; zOffset++) {
                        if (random.nextDouble() > 0.65) continue;
                        BlockPos pos = blockPosition().offset(xOffset, yOffset, zOffset);
                        if (!level.isLoaded(pos)) continue;
                        if (AbstractFireBlock.canBePlacedAt(level, pos, blockRayTraceResult.getDirection())) {
                            level.setBlockAndUpdate(pos, AbstractFireBlock.getState(level, pos));
                        }
                    }
                }
            }
            level.broadcastEntityEvent(this, (byte) 3);
            remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            ParticleUtils.spawnScatteringParticle(ParticleTypes.FLAME, level, position(), random, 0.3, 0.08, 24);
            ParticleUtils.spawnScatteringParticle(ParticleTypes.LARGE_SMOKE, level, position(), random, 0.22, 0.08, 12);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BLAZE_ROD;
    }
}
