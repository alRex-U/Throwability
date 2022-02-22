package com.alrexu.throwability.common.logic.player;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import javax.annotation.Nonnull;

public class Throw {
	public static void throwItem(@Nonnull Player player, float throwStrength, boolean all) {
		Level world = player.level;
		Inventory inventory = player.getInventory();
		ItemStack item = all ? inventory.getSelected() : inventory.getSelected().split(1);
		if (item.isEmpty() || !item.onDroppedByPlayer(player)) return;

		if (world.isClientSide()) {
			player.swing(InteractionHand.MAIN_HAND);
		}
		if (all) {
			inventory.removeItem(player.getInventory().getSelected());
		}

		ItemEntity itemEntity = new ItemEntity(
				world,
				player.getX(), player.getEyeY() - 0.3, player.getZ(),
				item);
		itemEntity.setPickUpDelay(20);
		itemEntity.setThrower(player.getUUID());

		float pitchSin = Mth.sin(player.getXRot() * ((float) Math.PI / 180F));
		float pitchCos = Mth.cos(player.getXRot() * ((float) Math.PI / 180F));
		float yawSin = Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
		float yawCos = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
		float random = player.getRandom().nextFloat() * ((float) Math.PI * 2F);
		float random2 = 0.02F * player.getRandom().nextFloat();
		itemEntity.setDeltaMovement(
				((-yawSin * pitchCos * 0.3F) + Math.cos((double) random) * (double) random2) * throwStrength,
				((-pitchSin * 0.3F + 0.1F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.1F)) * throwStrength,
				((yawCos * pitchCos * 0.3F) + Math.sin((double) random) * (double) random2) * throwStrength);

		ItemTossEvent event = new ItemTossEvent(itemEntity, player);
		if (MinecraftForge.EVENT_BUS.post(event)) return;

		player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, throwStrength / 4, 1.0f);

		if (!world.isClientSide()) {
			world.addFreshEntity(itemEntity);
		}
	}
}
