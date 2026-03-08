package com.alrex.throwability.mixin.client;

import com.alrex.throwability.client.input.KeyBindings;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntityMixin {
    public ClientPlayerEntityMixin(World world, BlockPos blockPos, float p_i241920_3_, GameProfile gameProfile) {
        super(world, blockPos, p_i241920_3_, gameProfile);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    public void onDrop(boolean dropAll, CallbackInfoReturnable<Boolean> cir) {
        if (!KeyBindings.throwAndDropAreConflicting()) return;
        AbstractThrowingAbility ability = this.getThrowAbility();
        if (ability.haveEnoughChargeTime()) {
            cir.setReturnValue(false);
        }
    }
}
