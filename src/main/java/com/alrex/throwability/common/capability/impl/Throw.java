package com.alrex.throwability.common.capability.impl;

import com.alrex.throwability.common.capability.IThrow;
import com.alrex.throwability.common.capability.ThrowType;
import com.alrex.throwability.common.network.ItemThrowMessage;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import javax.annotation.Nullable;

public class Throw implements IThrow {
	@Nullable
	final Player player;
	private float power = 0;
	private float oldPower = 0;
	private boolean charging = false;

	public Throw(Player player) {
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
		ItemStack stack = player.getInventory().getItem(inventoryIndex);
		Entity entity;
		if (type == ThrowType.One_As_Entity || type == ThrowType.One_As_Item) {
			stack = stack.split(1);
		} else {
			player.getInventory().removeItem(stack);
		}
		if (player.isLocalPlayer()) {
			ItemThrowMessage.send(player, inventoryIndex, strength, type);
			player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, strength, 1.0f);
			cancel();
		}
		player.swing(InteractionHand.MAIN_HAND);
		if (player.level.isClientSide()) return;
		if (type == ThrowType.One_As_Entity) {
			entity = ThrowUtil.getThrownEntityOf(
					player,
					stack,
					player.level,
					player.getEyePosition().add(0, -0.3, 0),
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
		if (entity instanceof ItemEntity itemEntity) {
			itemEntity.setPickUpDelay(20);
			itemEntity.setThrower(player.getUUID());
			ItemTossEvent event = new ItemTossEvent(itemEntity, player);
			if (MinecraftForge.EVENT_BUS.post(event)) return;
		}
		if (entity instanceof FallingBlockEntity || entity instanceof PrimedTnt) {
			strength *= 2.2f;
		} else if (entity instanceof ItemEntity) {
			strength *= 4;
		} else {
			strength *= 3;
		}
		float pitchSin = Mth.sin(player.getXRot() * ((float) Math.PI / 180F));
		float pitchCos = Mth.cos(player.getXRot() * ((float) Math.PI / 180F));
		float yawSin = Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
		float yawCos = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
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
