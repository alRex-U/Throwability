package com.alrex.throwability.client.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerModelAnimator {
    private final Player player;
    private final PlayerModel<?> model;
    private final boolean slim;
    private final float partial;
    private final float ageInTicks;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float netHeadYaw;
    private final float headPitch;

    public PlayerModelAnimator(
            Player player,
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

    public Player getPlayer() {
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

    public void setRotation(ModelPart part, float xRot, float yRot, float zRot, float factor) {
        part.xRot = Mth.rotLerp(factor, part.xRot, xRot);
        part.yRot = Mth.rotLerp(factor, part.yRot, yRot);
        part.zRot = Mth.rotLerp(factor, part.zRot, zRot);
    }

    public void setRotation(ModelPart part, float xRot, float yRot, float zRot) {
        part.xRot = xRot;
        part.yRot = yRot;
        part.zRot = zRot;
    }

    public void setRotation(ModelPart part, Rotation rotation, float factor) {
        setRotation(part, rotation.getXRot(), rotation.getYRot(), rotation.getZRot(), factor);
    }

    public void setRotation(ModelPart part, Rotation rotation) {
        setRotation(part, rotation.getXRot(), rotation.getYRot(), rotation.getZRot());
    }

    public void addRotation(ModelPart part, float xRot, float yRot, float zRot, float factor) {
        part.xRot += factor * xRot;
        part.yRot += factor * yRot;
        part.zRot += factor * zRot;
    }

    public void addRotation(ModelPart part, float xRot, float yRot, float zRot) {
        part.xRot += xRot;
        part.yRot += yRot;
        part.zRot += zRot;
    }

    public void addRotation(ModelPart part, Rotation rotation, float factor) {
        addRotation(part, rotation.getXRot(), rotation.getYRot(), rotation.getZRot(), factor);
    }

    public void addRotation(ModelPart part, Rotation rotation) {
        addRotation(part, rotation.getXRot(), rotation.getYRot(), rotation.getZRot());
    }

    public void bob(ModelPart part, float scale) {
        part.zRot += scale * Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        part.xRot += scale * Mth.sin(ageInTicks * 0.067F) * 0.05F;
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
