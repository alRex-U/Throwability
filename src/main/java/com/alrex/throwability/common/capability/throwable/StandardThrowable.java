package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Predicate;

public class StandardThrowable implements IThrowable {
    private static StandardThrowable INSTANCE = null;
    private final VanillaThrowableEntry[] vanillaThrowable = new VanillaThrowableEntry[]{
            new VanillaThrowableEntry(ArrowItem.class, new ArrowThrowable()),
            new VanillaThrowableEntry(item -> (item == Items.TNT), new TNTThrowable()),
            new VanillaThrowableEntry(EggItem.class, new EggThrowable()),
            new VanillaThrowableEntry(EnderPearlItem.class, new EnderPearlThrowable()),
            new VanillaThrowableEntry(ExperienceBottleItem.class, new ExperienceBottleThrowable()),
            new VanillaThrowableEntry(FireChargeItem.class, new FireChargeThrowable()),
            new VanillaThrowableEntry(FireworkRocketItem.class, new FireworkRocketThrowable()),
            new VanillaThrowableEntry(SnowballItem.class, new SnowballThrowable()),
            new VanillaThrowableEntry(ThrowablePotionItem.class, new ThrowablePotionThrowable()),
            new VanillaThrowableEntry(TridentItem.class, new TridentThrowable()),
            new VanillaThrowableEntry(BlockItem.class, new BlockThrowable()),
    };

    private StandardThrowable() {
    }

    public static StandardThrowable getInstance() {
        if (INSTANCE == null) INSTANCE = new StandardThrowable();
        return INSTANCE;
    }

    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargingTick) {
        Item item = stack.getItem();
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(item)) {
                return entry.getThrowable().throwAsEntity(thrower, stack, chargingTick);
            }
        }

        Entity entity = stack.getItem().createEntity(thrower.level, thrower, stack);
        if (entity == null) {
            entity = throwAsItem(thrower, stack, chargingTick);
        }
        Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
        Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
        double speedScale = 3. * MathHelper.clamp(chargingTick / (double) getMaxChargeTick(), 0, 1);

        entity.setPos(pos.x(), pos.y() + thrower.getEyeHeight() - 0.3, pos.z());
        entity.setDeltaMovement(throwVec.scale(speedScale));

        return entity;
    }

    private static class VanillaThrowableEntry {
        private final Predicate<Item> itemMatcher;
        private final IThrowable throwable;

        public VanillaThrowableEntry(Predicate<Item> itemMatcher, IThrowable throwable) {
            this.itemMatcher = itemMatcher;
            this.throwable = throwable;
        }

        public VanillaThrowableEntry(Class<? extends Item> itemCls, IThrowable throwable) {
            this.itemMatcher = item -> itemCls.isAssignableFrom(item.getClass());
            this.throwable = throwable;
        }

        public boolean matches(Item item) {
            return itemMatcher.test(item);
        }

        public IThrowable getThrowable() {
            return throwable;
        }
    }
}
