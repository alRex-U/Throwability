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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownGlowstoneDustEntity extends ProjectileItemEntity implements IRendersAsItem {
    public ThrownGlowstoneDustEntity(EntityType<? extends ThrownGlowstoneDustEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownGlowstoneDustEntity(World level, LivingEntity thrower) {
        super(EntityTypes.THROWN_GLOWSTONE_DUST.get(), thrower, level);
    }

    @Override
    protected void onHit(RayTraceResult rayTraceResult) {
        super.onHit(rayTraceResult);
        if (!level.isClientSide) {
            List<Entity> nearEntities = level.getEntities(this, getBoundingBox().inflate(4));
            for (Entity entity : nearEntities) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).addEffect(new EffectInstance(Effects.GLOWING, 200));
                }
            }
            level.broadcastEntityEvent(this, (byte) 3);
            remove();
        } else {
            Direction direction = (rayTraceResult instanceof BlockRayTraceResult)
                    ? ((BlockRayTraceResult) rayTraceResult).getDirection().getOpposite()
                    : null;
            ParticleUtils.spawnScatteringParticle(ParticleTypes.END_ROD, level, position(), random, 0.3, 0.08, 12, direction);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte eventID) {
        if (eventID == 3) {
            IParticleData particleData = ParticleUtils.getItemParticle(ParticleTypes.ITEM_SLIME, getItem());
            for (int i = 0; i < 8; i++) {
                level.addParticle(particleData, getX(), getY(), getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GLOWSTONE_DUST;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
