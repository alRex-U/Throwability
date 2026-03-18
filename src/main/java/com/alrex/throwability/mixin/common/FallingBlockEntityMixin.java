package com.alrex.throwability.mixin.common;

import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.utils.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity implements IThrown {

    @Shadow
    public boolean dropItem;
    @Shadow
    public CompoundNBT blockData;
    @Shadow
    private BlockState blockState;
    @Shadow
    private boolean cancelDrop;
    @Shadow
    public int time;

    @Unique
    private boolean throwability$thrown = false;
    @Unique
    private boolean throwability$oldHorizontalCollision = false;
    @Unique
    private boolean throwability$oldVerticalCollision = false;

    public FallingBlockEntityMixin(EntityType<?> type, World level) {
        super(type, level);
    }

    @Override
    public boolean isThrown() {
        return throwability$thrown;
    }

    @Override
    public void setThrown(boolean value) {
        throwability$thrown = value;
    }

    @Unique
    private static boolean throwability$tryPlaceBlock(World level, Block thisBlock, BlockState blockState, CompoundNBT blockData, BlockPos placedPosition, Direction collidedDirection, boolean concreteWithWater, boolean onGround) {
        DirectionalPlaceContext placeContext = new DirectionalPlaceContext(
                level, placedPosition, collidedDirection, ItemStack.EMPTY, collidedDirection.getOpposite()
        );
        BlockState placedPositionBlockState = level.getBlockState(placedPosition);
        boolean canReplace = placedPositionBlockState.canBeReplaced(placeContext);
        if (!canReplace) return false;

        boolean canSurvive = blockState.canSurvive(level, placedPosition);
        if (!canSurvive) {
            Item thisBlockItem = thisBlock.asItem();
            if (thisBlock.asItem() instanceof BlockItem) {
                blockState = ((BlockItem) thisBlockItem).getPlacementState(placeContext);
            }
            if (blockState == null || !blockState.canSurvive(level, placedPosition)) return false;
            thisBlock = blockState.getBlock();
        }
        boolean canStillFall = !concreteWithWater && onGround && (thisBlock instanceof FallingBlock) && FallingBlock.isFree(level.getBlockState(placedPosition.below()));
        if (canStillFall) return false;

        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && level.getFluidState(placedPosition).getType() == Fluids.WATER) {
            blockState = blockState.setValue(BlockStateProperties.WATERLOGGED, true);
        }

        if (level.setBlock(placedPosition, blockState, 3)) {
            if (blockData != null && blockState.hasTileEntity()) {
                TileEntity tileentity = level.getBlockEntity(placedPosition);
                if (tileentity != null) {
                    BlockUtils.applyTagToTileEntity(tileentity, blockData, blockState);
                }
            }
            return true;
        }
        return false;
    }

    @Unique
    private static boolean throwability$tryForceReplaceBlock(World level, BlockState blockState, CompoundNBT blockData, BlockPos placedPosition) {
        if (level.destroyBlock(placedPosition, true)
                && level.setBlock(placedPosition, blockState, 3)
        ) {
            if (blockData != null && blockState.hasTileEntity()) {
                TileEntity tileentity = level.getBlockEntity(placedPosition);
                if (tileentity != null) {
                    BlockUtils.applyTagToTileEntity(tileentity, blockData, blockState);
                }
            }
            return true;
        }
        return false;
    }

    @Unique
    private void throwability$dropAdditionalResources() {
        TileEntity tileEntity = blockState.createTileEntity(level);
        if (tileEntity instanceof IInventory) {
            BlockUtils.applyTagToTileEntity(tileEntity, blockData, blockState);
            InventoryHelper.dropContents(level, blockPosition(), (IInventory) tileEntity);
        }
    }

    @Unique
    private boolean throwability$checkDropByLifetime(BlockPos blockPos) {
        return this.time > 100 && (blockPos.getY() < 1 || blockPos.getY() > 256) || this.time > 600;
    }

    @Unique
    @Nullable
    private Direction throwability$getCollidedDirection(Vector3d movement) {
        if (!throwability$oldVerticalCollision && verticalCollision) {
            return getDeltaMovement().y > 0 ? Direction.UP : Direction.DOWN;
        } else if (!throwability$oldHorizontalCollision && horizontalCollision) {
            Vector3d deltaMovement = getDeltaMovement();
            if (Math.abs(movement.x()) > 1e-4 && Math.abs(deltaMovement.x()) <= 1e-4) {
                return movement.x() > 0 ? Direction.EAST : Direction.WEST;
            } else if (Math.abs(movement.z()) > 1e-4 && Math.abs(deltaMovement.z()) <= 1e-4) {
                return movement.z() > 0 ? Direction.SOUTH : Direction.NORTH;
            }
        }
        return null;
    }

    @Unique
    private void throwability$trySpawnAsBlock(Block thisBlock, BlockPos placedPosition, Direction collidedDirection, boolean concreteWithWater) {
        BlockState placedPositionBlockState = this.level.getBlockState(placedPosition);
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        if (placedPositionBlockState.is(Blocks.MOVING_PISTON)) return;

        this.remove();

        if (this.cancelDrop && thisBlock instanceof FallingBlock) {
            ((FallingBlock) thisBlock).onBroken(this.level, placedPosition, (FallingBlockEntity) (Object) this);
            return;
        }

        boolean hasTileEntity = thisBlock.hasTileEntity(blockState);

        if (!hasTileEntity) {
            if (throwability$tryPlaceBlock(level, thisBlock, blockState, blockData, placedPosition, collidedDirection, concreteWithWater, onGround)) {
                return;
            }
        } else {
            final int[] offsets = new int[]{0, 1, -1};
            for (int xOffset : offsets) {
                for (int yOffset : offsets) {
                    for (int zOffset : offsets) {
                        if (throwability$tryPlaceBlock(level, thisBlock, blockState, blockData, placedPosition.offset(xOffset, yOffset, zOffset), collidedDirection, concreteWithWater, onGround)) {
                            return;
                        }
                    }
                }
            }
            if (throwability$tryForceReplaceBlock(level, blockState, blockData, placedPosition)) {
                return;
            }
        }
        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            throwability$dropAdditionalResources();
            this.spawnAtLocation(thisBlock);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (!isThrown()) return;
        ci.cancel();

        if (this.blockState.isAir()) {
            this.remove();
            return;
        }

        Block thisBlock = blockState.getBlock();
        if (this.time++ == 0) {
            BlockPos blockPos = this.blockPosition();
            if (this.level.getBlockState(blockPos).is(thisBlock)) {
                this.level.removeBlock(blockPos, false);
            } else if (!this.level.isClientSide) {
                this.remove();
                return;
            }
        }

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        Direction collidedDirection;
        {
            throwability$oldHorizontalCollision = horizontalCollision;
            throwability$oldVerticalCollision = verticalCollision;
            Vector3d deltaMovement = getDeltaMovement();
            this.move(MoverType.SELF, this.getDeltaMovement());
            collidedDirection = throwability$getCollidedDirection(deltaMovement);
        }

        BlockPos placedPosition = blockPosition();
        boolean concreteWithWater = false;

        if (thisBlock instanceof ConcretePowderBlock) {
            if (getDeltaMovement().lengthSqr() > 1.0) {
                BlockRayTraceResult blockraytraceresult = this.level.clip(new RayTraceContext(new Vector3d(this.xo, this.yo, this.zo), this.position(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
                if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.level.getFluidState(blockraytraceresult.getBlockPos()).is(FluidTags.WATER)) {
                    placedPosition = blockraytraceresult.getBlockPos();
                    concreteWithWater = true;
                }
            } else {
                concreteWithWater = this.level.getFluidState(placedPosition).is(FluidTags.WATER);
            }
        }

        if (!horizontalCollision && !verticalCollision && !concreteWithWater) {
            if (throwability$checkDropByLifetime(placedPosition)) {
                if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(thisBlock);
                }
                this.remove();
            }
            return;
        }

        if (isAlive() && (this.horizontalCollision || this.verticalCollision) && (collidedDirection != null)) {
            throwability$trySpawnAsBlock(thisBlock, placedPosition, collidedDirection, concreteWithWater);
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }
}
