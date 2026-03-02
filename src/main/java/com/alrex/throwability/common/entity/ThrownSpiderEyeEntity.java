package com.alrex.throwability.common.entity;

import com.alrex.throwability.client.particle.ParticleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownSpiderEyeEntity extends ProjectileItemEntity implements IRendersAsItem {
    public ThrownSpiderEyeEntity(EntityType<? extends ThrownSpiderEyeEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownSpiderEyeEntity(World level, LivingEntity entity) {
        super(EntityTypes.THROWN_SPIDER_EYE.get(), entity, level);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);
        if (!level.isClientSide) {
            Entity entity = entityRayTraceResult.getEntity();
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new EffectInstance(Effects.POISON, 30, 2));
            }
            level.broadcastEntityEvent(this, (byte) 4);
            remove();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult blockRayTraceResult) {
        super.onHitBlock(blockRayTraceResult);
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 3);
            remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            IParticleData particleData = ParticleUtils.getItemParticle(null, getItem());
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
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
