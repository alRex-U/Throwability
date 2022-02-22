package com.alrexu.throwability.common.capability;


import net.minecraft.world.entity.player.Player;

public interface IThrow {
	public void chargeThrowPower();

	public void throwItem(Player player, boolean all);

	public void throwItem(Player player, boolean all, float strength);

	public void cancel();

	public boolean isCharging();

	public int getChargingPower();

	public float getStrength();

	public int getMaxPower();
}
