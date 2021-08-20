package com.alrexu.throwability.common.logic.player;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import javax.annotation.Nonnull;

public class Throw {
	public static void throwItem(@Nonnull PlayerEntity player, float throwStrength, boolean all) {
		World world = player.getEntityWorld();
		PlayerInventory inventory = player.inventory;
		ItemStack item = all ? inventory.getCurrentItem() : inventory.getCurrentItem().split(1);
		if (item.isEmpty() || !item.onDroppedByPlayer(player)) return;

		if (player.world.isRemote()) {
			player.swingArm(Hand.MAIN_HAND);
		}
		if (all) {
			inventory.removeStackFromSlot(player.inventory.currentItem);
		}

		ItemEntity itemEntity = new ItemEntity(
				world,
				player.getPosX(), player.getPosYEye() - 0.3, player.getPosZ(),
				item);
		itemEntity.setPickupDelay(20);
		itemEntity.setThrowerId(player.getUniqueID());

		float pitchSin = MathHelper.sin(player.rotationPitch * ((float) Math.PI / 180F));
		float pitchCos = MathHelper.cos(player.rotationPitch * ((float) Math.PI / 180F));
		float yawSin = MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F));
		float yawCos = MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F));
		float random = player.getRNG().nextFloat() * ((float) Math.PI * 2F);
		float random2 = 0.02F * player.getRNG().nextFloat();
		itemEntity.setMotion(
				((-yawSin * pitchCos * 0.3F) + Math.cos((double) random) * (double) random2) * throwStrength,
				((-pitchSin * 0.3F + 0.1F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.1F)) * throwStrength,
				((yawCos * pitchCos * 0.3F) + Math.sin((double) random) * (double) random2) * throwStrength);

		ItemTossEvent event = new ItemTossEvent(itemEntity, player);
		if (MinecraftForge.EVENT_BUS.post(event)) return;

		player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, throwStrength / 4, 1.0f);

		if (!world.isRemote())
			world.addEntity(itemEntity);
	}
}
