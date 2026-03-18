package com.alrex.throwability.extern.carryon;

import com.alrex.throwability.common.capability.throwable.StandardThrowable;
import com.alrex.throwability.extern.ExternalModManager;
import com.alrex.throwability.extern.carryon.throwable.CarryonBlockThrowable;
import com.alrex.throwability.extern.carryon.throwable.CarryonEntityThrowable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import tschipp.carryon.common.item.ItemCarryonBlock;
import tschipp.carryon.common.item.ItemCarryonEntity;

public class CarryOnManager extends ExternalModManager {
    public CarryOnManager() {
        super("carryon");
    }

    public boolean isCarryingOn(Player player) {
        if (!isInstalled()) return false;
        Item item = player.getMainHandItem().getItem();
        return item instanceof ItemCarryonEntity || item instanceof ItemCarryonBlock;
    }

    @Override
    public void init() {
        super.init();
        if (!isInstalled()) return;
        StandardThrowable.addThrowableHandler(CarryonEntityThrowable::match, new CarryonEntityThrowable());
        StandardThrowable.addThrowableHandler(CarryonBlockThrowable::match, new CarryonBlockThrowable());
    }
}
