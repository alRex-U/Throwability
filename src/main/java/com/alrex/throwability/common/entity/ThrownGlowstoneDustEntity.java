package com.alrex.throwability.common.entity;

import com.alrex.throwability.utils.VectorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
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
        } else {
            Vector3d pos = position();
            BlockRayTraceResult blockRayTraceResult = (rayTraceResult instanceof BlockRayTraceResult)
                    ? (BlockRayTraceResult) rayTraceResult
                    : null;
            for (int i = 0; i < 12; i++) {
                Vector3d movement = VectorUtil.getRandomNormalizedVec(random).scale(0.3 + 0.08 * random.nextDouble());
                if (blockRayTraceResult != null) {
                    switch (blockRayTraceResult.getDirection().getAxis()) {
                        case X:
                            if (((int) Math.signum(movement.x)) != blockRayTraceResult.getDirection().getAxisDirection().getStep())
                                movement = movement.multiply(-1, 1, 1);
                            break;
                        case Y:
                            if (((int) Math.signum(movement.y)) != blockRayTraceResult.getDirection().getAxisDirection().getStep())
                                movement = movement.multiply(1, -1, 1);
                            break;
                        case Z:
                            if (((int) Math.signum(movement.z)) != blockRayTraceResult.getDirection().getAxisDirection().getStep())
                                movement = movement.multiply(1, 1, -1);
                            break;
                    }
                }
                level.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, movement.x, movement.y, movement.z);
            }
        }
        remove();
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
