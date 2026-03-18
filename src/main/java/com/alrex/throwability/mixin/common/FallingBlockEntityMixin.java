package com.alrex.throwability.mixin.common;

import com.alrex.throwability.common.thrown.IThrown;
import com.alrex.throwability.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
    public CompoundTag blockData;
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

    public FallingBlockEntityMixin(EntityType<?> type, Level level) {
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
    private static boolean throwability$tryPlaceBlock(Level level, Block thisBlock, BlockState blockState, CompoundTag blockData, BlockPos placedPosition, Direction collidedDirection, boolean concreteWithWater, boolean onGround) {
        DirectionalPlaceContext placeContext = new DirectionalPlaceContext(
                level, placedPosition, collidedDirection, ItemStack.EMPTY, collidedDirection.getOpposite()
        );
        BlockState placedPositionBlockState = level.getBlockState(placedPosition);
        boolean canReplace = placedPositionBlockState.canBeReplaced(placeContext);
        if (!canReplace) return false;

        boolean canSurvive = blockState.canSurvive(level, placedPosition);
        if (!canSurvive) {
            Item thisBlockItem = thisBlock.asItem();
            if (thisBlock.asItem() instanceof BlockItem blockItem) {
                blockState = blockItem.getPlacementState(placeContext);
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
            if (blockData != null && blockState.hasBlockEntity()) {
                var tileentity = level.getBlockEntity(placedPosition);
                if (tileentity != null) {
                    BlockUtils.applyTagToTileEntity(tileentity, blockData, blockState);
                }
            }
            return true;
        }
        return false;
    }

    @Unique
    private static boolean throwability$tryForceReplaceBlock(Level level, BlockState blockState, CompoundTag blockData, BlockPos placedPosition) {
        if (level.destroyBlock(placedPosition, true)
                && level.setBlock(placedPosition, blockState, 3)
        ) {
            if (blockData != null && blockState.hasBlockEntity()) {
                var tileentity = level.getBlockEntity(placedPosition);
                if (tileentity != null) {
                    BlockUtils.applyTagToTileEntity(tileentity, blockData, blockState);
                }
            }
            return true;
        }
        return false;
    }

    @Unique
    private boolean throwability$checkDropByLifetime(BlockPos blockPos) {
        return this.time > 100 && (blockPos.getY() < 1 || blockPos.getY() > 256) || this.time > 600;
    }

    @Unique
    @Nullable
    private Direction throwability$getCollidedDirection(Vec3 movement) {
        if (!throwability$oldVerticalCollision && verticalCollision) {
            return getDeltaMovement().y > 0 ? Direction.UP : Direction.DOWN;
        } else if (!throwability$oldHorizontalCollision && horizontalCollision) {
            Vec3 deltaMovement = getDeltaMovement();
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

        discard();

        if (this.cancelDrop && thisBlock instanceof FallingBlock fallingBlock) {
            fallingBlock.onBrokenAfterFall(this.level, placedPosition, (FallingBlockEntity) (Object) this);
            return;
        }

        boolean hasTileEntity = thisBlock.defaultBlockState().hasBlockEntity();

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
            this.spawnAtLocation(thisBlock);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (!isThrown()) return;
        ci.cancel();

        if (this.blockState.isAir()) {
            discard();
            return;
        }

        Block thisBlock = blockState.getBlock();
        if (this.time++ == 0) {
            BlockPos blockPos = this.blockPosition();
            if (this.level.getBlockState(blockPos).is(thisBlock)) {
                this.level.removeBlock(blockPos, false);
            } else if (!this.level.isClientSide) {
                discard();
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
            Vec3 deltaMovement = getDeltaMovement();
            this.move(MoverType.SELF, this.getDeltaMovement());
            collidedDirection = throwability$getCollidedDirection(deltaMovement);
        }

        BlockPos placedPosition = blockPosition();
        boolean concreteWithWater = false;

        if (thisBlock instanceof ConcretePowderBlock) {
            if (getDeltaMovement().lengthSqr() > 1.0) {
                var blockHitResult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                if (blockHitResult.getType() != HitResult.Type.MISS && this.level.getFluidState(blockHitResult.getBlockPos()).is(FluidTags.WATER)) {
                    placedPosition = blockHitResult.getBlockPos();
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
                discard();
            }
            return;
        }

        if (isAlive() && (this.horizontalCollision || this.verticalCollision) && (collidedDirection != null)) {
            throwability$trySpawnAsBlock(thisBlock, placedPosition, collidedDirection, concreteWithWater);
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }
}
