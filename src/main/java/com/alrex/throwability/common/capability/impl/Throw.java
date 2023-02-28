package com.alrex.throwability.common.capability.impl;

import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.common.capability.ThrowType;
import com.alrex.throwability.common.network.ItemThrowMessage;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import javax.annotation.Nullable;

public class Throw implements IThrow {
	@Nullable
	final PlayerEntity player;
	private float power = 0;
	private float oldPower = 0;
	private boolean charging = false;

	public Throw(PlayerEntity player) {
		this.player = player;
	}

	public Throw() {
		player = null;
	}

	@Override
	public void cancel() {
		charging = false;
		power = 0;
	}

	@Override
	public boolean isCharging() {
		return charging;
	}

	@Override
	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	@Override
	public float getChargingPower() {
		return power;
	}

	@Override
	public float getMaxPower() {
		return 1;
	}

	@Override
	public void tick() {
		oldPower = power;
	}

	@Override
	public float getOldPower() {
		return oldPower;
	}

	@Override
	public void chargeThrowPower() {
		charging = true;
		if (power < getMaxPower()) power += 0.05f;
	}

	@Override
	public void throwItem(int inventoryIndex, ThrowType type, float strength) {
		if (player == null) return;
		ItemStack stack = player.inventory.getItem(inventoryIndex);
		Entity entity;
		if (type == ThrowType.One_As_Entity || type == ThrowType.One_As_Item) {
			stack = stack.split(1);
		} else {
			player.inventory.removeItem(stack);
		}
		if (player.isLocalPlayer()) {
			ItemThrowMessage.send(player, inventoryIndex, strength, type);
			player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, strength, 1.0f);
			cancel();
		}
		player.swing(Hand.MAIN_HAND);
		if (player.level.isClientSide()) return;
		if (type == ThrowType.One_As_Entity) {
			entity = ThrowUtil.getThrownEntityOf(
					player,
					stack,
					player.level,
					player.position().add(0, player.getEyeHeight() - 0.3, 0),
					player.getLookAngle()
			);
		} else {
			entity = ThrowUtil.getItemEntity(
					stack,
					player.level,
					player.position()
							.add(0, player.getEyeHeight() - 0.3, 0)
			);
		}
		if (entity instanceof ItemEntity) {
			ItemEntity itemEntity = (ItemEntity) entity;
			itemEntity.setPickUpDelay(20);
			itemEntity.setThrower(player.getUUID());
			ItemTossEvent event = new ItemTossEvent(itemEntity, player);
			if (MinecraftForge.EVENT_BUS.post(event)) return;
		}
		if (entity instanceof FallingBlockEntity || entity instanceof TNTEntity) {
			strength *= 2.2f;
		} else if (entity instanceof ItemEntity) {
			strength *= 4;
		} else {
			strength *= 3;
		}
		float pitchSin = MathHelper.sin(player.xRot * ((float) Math.PI / 180F));
		float pitchCos = MathHelper.cos(player.xRot * ((float) Math.PI / 180F));
		float yawSin = MathHelper.sin(player.yRot * ((float) Math.PI / 180F));
		float yawCos = MathHelper.cos(player.yRot * ((float) Math.PI / 180F));
		float random = player.getRandom().nextFloat() * ((float) Math.PI * 2F);
		float random2 = 0.02F * player.getRandom().nextFloat();
		entity.setDeltaMovement(
				((-yawSin * pitchCos * 0.3F) + 0.5f * Math.cos(random) * (double) random2) * strength,
				((-pitchSin * 0.3F + 0.05 + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.05F)) * strength,
				((yawCos * pitchCos * 0.3F) + 0.5f * Math.sin(random) * (double) random2) * strength);
		player.level.addFreshEntity(entity);
	}

	@Override
	public void setPower(float value) {
		power = value;
	}
}
