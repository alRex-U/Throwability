package com.alrex.throwability.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownGunPowderEntity extends ProjectileItemEntity implements IRendersAsItem {
    public ThrownGunPowderEntity(EntityType<? extends ThrownGunPowderEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownGunPowderEntity(World level, LivingEntity entity) {
        super(EntityTypes.THROWN_GUNPOWDER.get(), entity, level);
    }

    @Override
    protected void onHit(RayTraceResult p_70227_1_) {
        super.onHit(p_70227_1_);

        if (!level.isClientSide) {
            Vector3d pos = position();
            level.explode(this, pos.x, pos.y, pos.z, 0.8f, Explosion.Mode.NONE);
        }

        remove();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GUNPOWDER;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
