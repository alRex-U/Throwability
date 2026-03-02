package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownSlimeballEntity extends ProjectileItemEntity implements IRendersAsItem {
    private int boundingCount = 0;

    public ThrownSlimeballEntity(EntityType<? extends ThrownSlimeballEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownSlimeballEntity(World level, LivingEntity entity) {
        super(EntityTypes.THROWN_SLIMEBALL.get(), entity, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            if (this.tickCount % 4 == 0) {
                ParticleUtils.spawnScatteringParticle(ParticleTypes.ITEM_SLIME, level, position(), random, 0.1, 0.04, 1, getDeltaMovement());
            }
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) {
        super.onHitBlock(blockRayTraceResult);
        boundingCount++;
        if (boundingCount > 3) {
            if (!level.isClientSide) {
                level.broadcastEntityEvent(this, (byte) 3);
                remove();
            }
        } else {
            Vector3d normal = VectorUtil.create3dFrom3i(blockRayTraceResult.getDirection().getNormal());
            setDeltaMovement(VectorUtil.reflect(getDeltaMovement(), normal).scale(0.9));
        }
    }

    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            IParticleData particleData = ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem());
            for (int i = 0; i < 8; i++) {
                level.addParticle(particleData, getX(), getY(), getZ(), 0, 0, 0);
            }
        } else if (eventID == 4) {
            ParticleUtils.spawnScatteringParticle(
                    ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem()),
                    level, position(), random, 0.2, 0.08, 16
            );
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);
        Entity entity = entityRayTraceResult.getEntity();
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 4));
            level.broadcastEntityEvent(this, (byte) 4);
            remove();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Bound", boundingCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        boundingCount = tag.getInt("Bound");
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SLIME_BALL;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
