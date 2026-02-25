package com.alrex.throwability.client.render.entity;

import com.alrex.throwability.common.entity.ThrownWeaponEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeaponEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/arrow");

    protected ThrownWeaponRenderer(EntityRendererManager entityRendererManager) {
        super(entityRendererManager);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownWeaponEntity thrownWeaponEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(ThrownWeaponEntity thrownWeaponEntity, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLight) {
        ItemStack itemStack = thrownWeaponEntity.getWeapon();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        matrixStack.pushPose();
        {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTick, thrownWeaponEntity.yRotO, thrownWeaponEntity.yRot) - 90f));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTick, thrownWeaponEntity.xRotO, thrownWeaponEntity.xRot) - 45f));
            IBakedModel itemModel = itemRenderer.getModel(itemStack, thrownWeaponEntity.level, null);
            itemRenderer.render(itemStack, ItemCameraTransforms.TransformType.NONE, false, matrixStack, renderTypeBuffer, packedLight, OverlayTexture.NO_OVERLAY, itemModel);
        }
        matrixStack.popPose();
    }
}
