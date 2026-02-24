package com.alrex.throwability.mixin.common;

import com.alrex.throwability.common.thrown.ICollidedDirectionProvider;
import com.alrex.throwability.common.thrown.IThrown;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


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
    public abstract BlockState getBlockState();

    @Unique
    private boolean throwability$thrown = false;

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

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickReturn(CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (!isThrown()) return;
        Block thisBlock = blockState.getBlock();
        Direction collidedDirection;
        if (isAlive()
                && !(thisBlock instanceof FallingBlock)
                && this.horizontalCollision
                && this instanceof ICollidedDirectionProvider
                && (collidedDirection = ((ICollidedDirectionProvider) this).getCollidedDirection()) != null
        ) {
            BlockPos placedPosition = blockPosition();
            BlockState placedPositionBlockState = this.level.getBlockState(placedPosition);
            if (placedPositionBlockState.is(Blocks.MOVING_PISTON)) return;

            this.remove();
            if (!this.cancelDrop) {
                DirectionalPlaceContext placeContext = new DirectionalPlaceContext(
                        this.level, placedPosition, collidedDirection, ItemStack.EMPTY, collidedDirection
                );
                boolean canBePlaced = placedPositionBlockState.canBeReplaced(placeContext);
                boolean isPlaceable = this.blockState.canSurvive(this.level, placedPosition);

                BlockState customBlockState = this.blockState;
                if (canBePlaced && !isPlaceable) {
                    Item thisBlockItem = thisBlock.asItem();
                    if (thisBlock.asItem() instanceof BlockItem) {
                        customBlockState = ((BlockItem) thisBlockItem).getPlacementState(placeContext);
                    }
                    isPlaceable = customBlockState != null && customBlockState.canSurvive(this.level, placedPosition);
                }
                if (canBePlaced && isPlaceable && customBlockState != null) {
                    this.blockState = customBlockState;
                    thisBlock = blockState.getBlock();

                    if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(placedPosition).getType() == Fluids.WATER) {
                        this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                    }

                    if (this.level.setBlock(placedPosition, this.blockState, 3)) {

                        if (this.blockData != null && this.blockState.hasTileEntity()) {
                            TileEntity tileentity = this.level.getBlockEntity(placedPosition);
                            if (tileentity != null) {
                                CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());
                                for (String key : blockData.getAllKeys()) {
                                    INBT inbt = this.blockData.get(key);
                                    if (!"x".equals(key) && !"y".equals(key) && !"z".equals(key)) {
                                        if (inbt != null) {
                                            compoundnbt.put(key, inbt.copy());
                                        }
                                    }
                                }

                                tileentity.load(this.blockState, compoundnbt);
                                tileentity.setChanged();
                            }
                        }
                    } else {
                        if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(thisBlock);
                        }
                    }
                } else {
                    if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(thisBlock);
                    }
                }
            }

        }

    }
}
