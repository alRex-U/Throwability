package com.alrex.throwability.client.render.entity;

import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.common.model.TransformationHelper;

public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeaponEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/arrow");

    protected ThrownWeaponRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownWeaponEntity thrownWeaponEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(ThrownWeaponEntity thrownWeaponEntity, float p_225623_2_, float partialTick, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight) {
        var itemStack = thrownWeaponEntity.getWeapon();
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        matrixStack.pushPose();
        {
            var itemModel = itemRenderer.getModel(itemStack, thrownWeaponEntity.level, null, thrownWeaponEntity.getId());
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, thrownWeaponEntity.yRotO, thrownWeaponEntity.getYRot()) + 180f));
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTick, thrownWeaponEntity.xRotO, thrownWeaponEntity.getXRot()) - 90f + 15f));
            matrixStack.mulPose(TransformationHelper.quatFromXYZ(itemModel.getTransforms().thirdPersonRightHand.rotation, true));
            itemRenderer.render(itemStack, ItemTransforms.TransformType.NONE, false, matrixStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, itemModel);
        }
        matrixStack.popPose();
        super.render(thrownWeaponEntity, p_225623_2_, partialTick, matrixStack, multiBufferSource, packedLight);
    }
}
