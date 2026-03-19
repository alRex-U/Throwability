package com.alrex.throwability.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownDispensibleContainerEntity extends ThrowableItemProjectile {
    public ThrownDispensibleContainerEntity(EntityType<? extends ThrownDispensibleContainerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownDispensibleContainerEntity(Level level, LivingEntity thrower, ItemStack dispensibleContainer) {
        super(EntityTypes.THROWN_BUCKET.get(), thrower, level);
        setItem(dispensibleContainer.copy());
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        ItemStack bucket = getItem();

        if (bucket.getItem() instanceof DispensibleContainerItem dispensibleContainerItem) {
            var hitBlockPos = blockHitResult.getBlockPos();
            if (level().isClientSide()) {
                if (dispensibleContainerItem instanceof BucketItem bucketItem) {
                    bucketItem.playEmptySound(null, level(), hitBlockPos);
                }
            } else {
                BlockPos actualBlockPos;
                if (dispensibleContainerItem instanceof BucketItem bucketItem) {
                    actualBlockPos = bucketItem.canBlockContainFluid(level(), hitBlockPos, level().getBlockState(hitBlockPos)) ? hitBlockPos : hitBlockPos.relative(blockHitResult.getDirection());
                } else {
                    actualBlockPos = hitBlockPos.relative(blockHitResult.getDirection());
                }
                Player owner = getOwner() instanceof Player player ? player : null;
                if (dispensibleContainerItem.emptyContents(owner, level(), actualBlockPos, blockHitResult)) {
                    dispensibleContainerItem.checkExtraContent(owner, level(), bucket, actualBlockPos);
                    spawnAtLocation(new ItemStack(Items.BUCKET));
                } else {
                    spawnAtLocation(getItem().copy());
                }
            }
        }
        if (!level().isClientSide()) {
            discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.BUCKET;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
