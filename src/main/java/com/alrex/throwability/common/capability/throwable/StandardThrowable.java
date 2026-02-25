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
            new VanillaThrowableEntry(item -> (item.getItem() == Items.TNT), new TNTThrowable()),
            new VanillaThrowableEntry(EggItem.class, new EggThrowable()),
            new VanillaThrowableEntry(EnderPearlItem.class, new EnderPearlThrowable()),
            new VanillaThrowableEntry(ExperienceBottleItem.class, new ExperienceBottleThrowable()),
            new VanillaThrowableEntry(FireChargeItem.class, new FireChargeThrowable()),
            new VanillaThrowableEntry(FireworkRocketItem.class, new FireworkRocketThrowable()),
            new VanillaThrowableEntry(SnowballItem.class, new SnowballThrowable()),
            new VanillaThrowableEntry(ThrowablePotionItem.class, new ThrowablePotionThrowable()),
            new VanillaThrowableEntry(TridentItem.class, new TridentThrowable()),
            new VanillaThrowableEntry(BlockItem.class, new BlockThrowable()),
            new VanillaThrowableEntry(WeaponThrowable::hasAttackDamage, new WeaponThrowable())
    };

    private StandardThrowable() {
    }

    public static StandardThrowable getInstance() {
        if (INSTANCE == null) INSTANCE = new StandardThrowable();
        return INSTANCE;
    }

    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargingTick) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().throwAsEntity(thrower, stack, chargingTick);
            }
        }

        Entity entity = stack.getItem().createEntity(thrower.level, thrower, stack);
        if (entity == null) {
            return throwAsItem(thrower, stack, chargingTick);
        }
        Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
        Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
        double speedScale = 4. * MathHelper.clamp(chargingTick / (double) getMaxChargeTick(), 0, 1);

        entity.setPos(pos.x(), pos.y() + thrower.getEyeHeight() - 0.3, pos.z());
        entity.setDeltaMovement(throwVec.scale(speedScale));

        return entity;
    }

    @Override
    public boolean canThrowableNow(PlayerEntity thrower, ItemStack stack) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().canThrowableNow(thrower, stack);
            }
        }
        return true;
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
        Item item = stack.getItem();
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                entry.getThrowable().onThrownOnClient(thrower, stack);
                return;
            }
        }
    }

    private static class VanillaThrowableEntry {
        private final Predicate<ItemStack> itemMatcher;
        private final IThrowable throwable;

        public VanillaThrowableEntry(Predicate<ItemStack> itemMatcher, IThrowable throwable) {
            this.itemMatcher = itemMatcher;
            this.throwable = throwable;
        }

        public VanillaThrowableEntry(Class<? extends Item> itemCls, IThrowable throwable) {
            this.itemMatcher = itemStack -> itemCls.isAssignableFrom(itemStack.getItem().getClass());
            this.throwable = throwable;
        }

        public boolean matches(ItemStack item) {
            return itemMatcher.test(item);
        }

        public IThrowable getThrowable() {
            return throwable;
        }
    }
}
