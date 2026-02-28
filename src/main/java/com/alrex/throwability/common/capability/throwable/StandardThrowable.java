package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StandardThrowable implements IThrowable {
    private static StandardThrowable INSTANCE = null;
    private static final ArrayList<Supplier<VanillaThrowableEntry>> vanillaThrowableRegistry = new ArrayList<>(Arrays.asList(
            () -> new VanillaThrowableEntry(ArrowItem.class, new ArrowThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.TNT), new TNTThrowable()),
            () -> new VanillaThrowableEntry(EggItem.class, new EggThrowable()),
            () -> new VanillaThrowableEntry(EnderPearlItem.class, new EnderPearlThrowable()),
            () -> new VanillaThrowableEntry(ExperienceBottleItem.class, new ExperienceBottleThrowable()),
            () -> new VanillaThrowableEntry(FireChargeItem.class, new FireChargeThrowable()),
            () -> new VanillaThrowableEntry(FireworkRocketItem.class, new FireworkRocketThrowable()),
            () -> new VanillaThrowableEntry(SnowballItem.class, new SnowballThrowable()),
            () -> new VanillaThrowableEntry(ThrowablePotionItem.class, new ThrowablePotionThrowable()),
            () -> new VanillaThrowableEntry(TridentItem.class, new TridentThrowable()),
            () -> new VanillaThrowableEntry(BlockItem.class, new BlockThrowable()),
            () -> new VanillaThrowableEntry(WeaponThrowable::hasAttackDamage, new WeaponThrowable())
    ));

    private final VanillaThrowableEntry[] vanillaThrowable;

    private StandardThrowable() {
        vanillaThrowable = vanillaThrowableRegistry.stream().map(Supplier::get).toArray(VanillaThrowableEntry[]::new);
    }

    /// Add throwable entry to standard throwable handler
    ///
    /// Note : This have be used only in loading process and only once for single throwable,
    /// and only for existing items.
    /// If you want to add custom throwable to new item, you should attach IThrowable capability to the item.
    public static void addThrowableHandler(Predicate<ItemStack> itemMatcher, IThrowable throwable) {
        vanillaThrowableRegistry.add(() -> new VanillaThrowableEntry(itemMatcher, throwable));
    }

    public static StandardThrowable getInstance() {
        if (INSTANCE == null) INSTANCE = new StandardThrowable();
        return INSTANCE;
    }

    @Override
    public Entity throwAsEntity(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().throwAsEntity(thrower, stack, chargedTick);
            }
        }

        Entity entity = stack.getItem().createEntity(thrower.level, thrower, stack);
        if (entity == null) {
            return throwAsItem(thrower, stack, chargedTick);
        }
        Vector3d pos = ThrowUtil.getBasicThrowingPosition(thrower);
        Vector3d throwVec = ThrowUtil.getBasicThrowingVector(thrower);
        double speedScale = 4. * MathHelper.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

        entity.setPos(pos.x(), pos.y() + thrower.getEyeHeight() - 0.3, pos.z());
        entity.setDeltaMovement(throwVec.scale(speedScale));

        return entity;
    }

    @Override
    public Entity throwAsItem(PlayerEntity thrower, ItemStack stack, int chargedTick) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().throwAsItem(thrower, stack, chargedTick);
            }
        }

        return IThrowable.super.throwAsItem(thrower, stack, chargedTick);
    }

    @Override
    public int getMaxChargeTick(ItemStack stack) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().getMaxChargeTick(stack);
            }
        }
        return IThrowable.super.getMaxChargeTick(stack);
    }

    @Override
    public boolean canThrowableNow(PlayerEntity thrower, ItemStack stack) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().canThrowableNow(thrower, stack);
            }
        }
        return IThrowable.super.canThrowableNow(thrower, stack);
    }

    @Override
    public void onThrownOnClient(PlayerEntity thrower, ItemStack stack) {
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
