package com.alrex.throwability.client.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PlayerModelAnimator {
    private final PlayerEntity player;
    private final PlayerModel<?> model;
    private final boolean slim;
    private final float partial;
    private final float ageInTicks;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float netHeadYaw;
    private final float headPitch;

    public PlayerModelAnimator(
            PlayerEntity player,
            PlayerModel<?> model,
            boolean slim,
            float ageInTicks,
            float limbSwing,
            float limbSwingAmount,
            float netHeadYaw,
            float headPitch
    ) {
        this.player = player;
        this.model = model;
        this.slim = slim;
        this.partial = Minecraft.getInstance().getFrameTime();
        this.ageInTicks = ageInTicks;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public PlayerModel<?> getModel() {
        return model;
    }

    public float getPartialTick() {
        return partial;
    }

    public boolean isSlim() {
        return slim;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getHeadYaw() {
        return netHeadYaw;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public void setRotation(ModelRenderer renderer, float xRot, float yRot, float zRot, float factor) {
        renderer.xRot = MathHelper.rotLerp(factor, renderer.xRot, xRot);
        renderer.yRot = MathHelper.rotLerp(factor, renderer.yRot, yRot);
        renderer.zRot = MathHelper.rotLerp(factor, renderer.zRot, zRot);
    }

    public void setRotation(ModelRenderer renderer, float xRot, float yRot, float zRot) {
        renderer.xRot = xRot;
        renderer.yRot = yRot;
        renderer.zRot = zRot;
    }

    public void setRotation(ModelRenderer renderer, Rotation rotation, float factor) {
        setRotation(renderer, rotation.getXRot(), rotation.getYRot(), rotation.getZRot(), factor);
    }

    public void setRotation(ModelRenderer renderer, Rotation rotation) {
        setRotation(renderer, rotation.getXRot(), rotation.getYRot(), rotation.getZRot());
    }

    public void addRotation(ModelRenderer renderer, float xRot, float yRot, float zRot, float factor) {
        renderer.xRot += factor * xRot;
        renderer.yRot += factor * yRot;
        renderer.zRot += factor * zRot;
    }

    public void addRotation(ModelRenderer renderer, float xRot, float yRot, float zRot) {
        renderer.xRot += xRot;
        renderer.yRot += yRot;
        renderer.zRot += zRot;
    }

    public void addRotation(ModelRenderer renderer, Rotation rotation, float factor) {
        addRotation(renderer, rotation.getXRot(), rotation.getYRot(), rotation.getZRot(), factor);
    }

    public void addRotation(ModelRenderer renderer, Rotation rotation) {
        addRotation(renderer, rotation.getXRot(), rotation.getYRot(), rotation.getZRot());
    }

    public void bob(ModelRenderer renderer, float scale) {
        renderer.zRot += scale * MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        renderer.xRot += scale * MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    }

    public void copyFromBodyToWear() {
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftPants.copyFrom(model.leftLeg);
        model.jacket.copyFrom(model.body);
        model.hat.copyFrom(model.head);
    }
}
