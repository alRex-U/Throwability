package com.alrex.throwability.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ThrownBucketEntity extends ProjectileItemEntity implements IRendersAsItem {
    public ThrownBucketEntity(EntityType<? extends ThrownBucketEntity> entityType, World level) {
        super(entityType, level);
    }

    public ThrownBucketEntity(World level, LivingEntity thrower, ItemStack bucket) {
        super(EntityTypes.THROWN_BUCKET.get(), thrower, level);
        setItem(bucket.copy());
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult rayTraceResult) {
        super.onHitBlock(rayTraceResult);
        ItemStack bucket = getItem();
        if (bucket.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) bucket.getItem();
            Fluid fluid = item.getFluid();
            if (fluid != Fluids.EMPTY) {
                BlockPos hitBlockPos = rayTraceResult.getBlockPos();
                BlockPos actualBlockPos = item.canBlockContainFluid(level, hitBlockPos, level.getBlockState(hitBlockPos)) ? hitBlockPos : hitBlockPos.relative(rayTraceResult.getDirection());
                Entity entity = getOwner();
                if (item.emptyBucket(entity instanceof PlayerEntity ? (PlayerEntity) entity : null, level, actualBlockPos, rayTraceResult)) {
                    item.checkExtraContent(level, bucket, actualBlockPos);
                    if (entity != null) {
                        SoundEvent soundevent = fluid.getAttributes().getEmptySound();
                        if (soundevent == null) {
                            soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                        }
                        entity.playSound(soundevent, 1.0F, 1.0F);
                    }
                    spawnAtLocation(new ItemStack(Items.BUCKET));
                }
            }
        }
        remove();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BUCKET;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
