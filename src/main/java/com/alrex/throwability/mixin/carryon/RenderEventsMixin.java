package com.alrex.throwability.mixin.carryon;

import com.alrex.throwability.client.animation.impl.ThrowingAnimation;
import com.alrex.throwability.common.ability.AbstractThrowingAbility;
import com.alrex.throwability.common.ability.IThrowabilityProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.event.RenderEvents;
import tschipp.carryon.common.handler.RegistrationHandler;
import tschipp.carryon.common.helper.ScriptParseHelper;
import tschipp.carryon.common.scripting.CarryOnOverride;
import tschipp.carryon.common.scripting.ScriptChecker;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderEvents.class)
public abstract class RenderEventsMixin {
    @Nullable
    @Unique
    private PlayerEntity throwability$drawingPlayer = null;
    @Unique
    private float throwability$currentPartialTick = 0;

    @Inject(method = "applyGeneralTransformations", at = @At("TAIL"), remap = false)
    private void onApplyGeneralTransformations(PlayerEntity player, float partialTicks, MatrixStack matrix, CallbackInfo ci) {
        if (!(player instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility abstractThrowingAbility = ((IThrowabilityProvider) player).getThrowAbility();
        if (!abstractThrowingAbility.isCharging()) return;
        CarryOnOverride overrider = ScriptChecker.getOverride(player);
        float xRot;
        if (overrider == null) {
            ItemStack stack = player.getMainHandItem();
            xRot = 2.0F + (RenderEvents.doSneakCheck(player) ? 0.0F : 0.2F) - (stack.getItem() == RegistrationHandler.itemEntity ? 0.3F : 0.0F);
        } else {
            xRot = ScriptParseHelper.getXYZArray(overrider.getRenderRotationLeftArm())[0];
        }

        float factor = ThrowingAnimation.getFactor(abstractThrowingAbility, partialTicks);
        xRot -= (float) Math.toRadians(factor * 90);
        matrix.translate(
                0,
                0.4 + 0.71 * Math.cos(xRot) + 0.7 * factor,
                -0.65 + 0.71 * Math.sin(xRot) + 0.2 * factor
        );
    }


    @Inject(method = "drawArms", at = @At("HEAD"), remap = false)
    private void onDrawArmsHead(PlayerEntity player, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, CallbackInfo ci) {
        throwability$drawingPlayer = player;
        throwability$currentPartialTick = partialTicks;
    }

    @Inject(method = "drawArms", at = @At("RETURN"), remap = false)
    private void onDrawArmsTail(PlayerEntity player, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, CallbackInfo ci) {
        throwability$drawingPlayer = null;
    }

    @Inject(method = "renderArmPost", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/model/ModelRenderer;render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;II)V"), remap = false)
    private void onRenderArm(ModelRenderer arm, float x, float z, boolean right, boolean sneaking, int light, MatrixStack matrix, IVertexBuilder builder, CallbackInfo ci) {
        if (!(throwability$drawingPlayer instanceof IThrowabilityProvider)) return;
        AbstractThrowingAbility abstractThrowingAbility = ((IThrowabilityProvider) throwability$drawingPlayer).getThrowAbility();
        if (!abstractThrowingAbility.isCharging()) return;
        float factor = ThrowingAnimation.getFactor(abstractThrowingAbility, throwability$currentPartialTick);
        arm.xRot += factor * (float) Math.toRadians(-90);
    }
}
