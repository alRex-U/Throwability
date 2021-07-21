package com.alrexu.throwability.common.capability.impl;

import com.alrexu.throwability.common.capability.IThrow;
import net.minecraft.entity.player.PlayerEntity;

public class Throw implements IThrow {
	private int power = 0;
	private boolean charging = false;

	@Override
	public void throwItem(PlayerEntity player, boolean all) {
		charging = false;
		com.alrexu.throwability.common.logic.player.Throw.throwItem(player, getStrength(), all);
		power = 0;
	}

	@Override
	public void throwItem(PlayerEntity player, boolean all, float strength) {
		charging = false;
		com.alrexu.throwability.common.logic.player.Throw.throwItem(player, strength, all);
		power = 0;
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
	public int getChargingPower() {
		return power;
	}

	@Override
	public float getStrength() {
		return power / 5.0f;
	}

	@Override
	public int getMaxPower() {
		return 20;
	}

	@Override
	public void chargeThrowPower() {
		charging = true;
		if (power < getMaxPower()) power++;
	}
}
