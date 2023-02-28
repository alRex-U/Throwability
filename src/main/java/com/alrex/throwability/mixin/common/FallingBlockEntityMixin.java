package com.alrex.throwability.mixin.common;

import com.alrex.throwability.shrewd.FallingBlockEntitySub;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
	@Shadow
	private BlockState blockState;

	public FallingBlockEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
		super(p_19870_, p_19871_);
	}

	@Inject(method = "Lnet/minecraft/world/entity/item/FallingBlockEntity;setStartPos(Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
	public void onSetStartPos(BlockPos value, CallbackInfo ci) {
		if (FallingBlockEntitySub.valueShouldSet) {
			blockState = FallingBlockEntitySub.state;
		}
	}
}
