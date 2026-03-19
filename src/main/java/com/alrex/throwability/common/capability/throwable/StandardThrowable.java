package com.alrex.throwability.common.capability.throwable;

import com.alrex.throwability.common.ability.ThrowType;
import com.alrex.throwability.common.capability.IThrowable;
import com.alrex.throwability.utils.ThrowUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StandardThrowable implements IThrowable {
    private static StandardThrowable INSTANCE = null;
    private static final ArrayList<Supplier<VanillaThrowableEntry>> VANILLA_THROWABLE_REGISTRY = new ArrayList<>(Arrays.asList(
            () -> new VanillaThrowableEntry(ArrowItem.class, new ArrowThrowable()),
            () -> new VanillaThrowableEntry(EggItem.class, new EggThrowable()),
            () -> new VanillaThrowableEntry(EnderpearlItem.class, new EnderPearlThrowable()),
            () -> new VanillaThrowableEntry(ExperienceBottleItem.class, new ExperienceBottleThrowable()),
            () -> new VanillaThrowableEntry(FireChargeItem.class, new FireChargeThrowable()),
            () -> new VanillaThrowableEntry(FireworkRocketItem.class, new FireworkRocketThrowable()),
            () -> new VanillaThrowableEntry(SnowballItem.class, new SnowballThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.TNT), new TNTThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.GLOWSTONE_DUST), new GlowstoneDustThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.BLAZE_POWDER), new BlazePowderThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.BLAZE_ROD), new BlazeRodThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.GUNPOWDER), new GunPowderThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.GHAST_TEAR), new GhastTearThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.SLIME_BALL), new SlimeballThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.INK_SAC), new InkSacThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.GLOW_INK_SAC), new GlowInkSacThrowable()),
            () -> new VanillaThrowableEntry(item -> (item.getItem() == Items.SPIDER_EYE), new SpiderEyeThrowable()),
            () -> new VanillaThrowableEntry(BucketItem.class, new DispensibleContainerThrowable()),
            () -> new VanillaThrowableEntry(SolidBucketItem.class, new DispensibleContainerThrowable()),
            () -> new VanillaThrowableEntry(ThrowablePotionItem.class, new ThrowablePotionThrowable()),
            () -> new VanillaThrowableEntry(TridentItem.class, new TridentThrowable()),
            () -> new VanillaThrowableEntry(SpawnEggItem.class, new SpawnEggThrowable()),
            () -> new VanillaThrowableEntry(BlockItem.class, new BlockThrowable()),
            () -> new VanillaThrowableEntry(WeaponThrowable::hasAttackDamage, new WeaponThrowable())
    ));
    private static boolean REGISTRY_FREEZE = false;

    private final VanillaThrowableEntry[] vanillaThrowable;

    private StandardThrowable() {
        vanillaThrowable = VANILLA_THROWABLE_REGISTRY.stream().map(Supplier::get).toArray(VanillaThrowableEntry[]::new);
    }

    /// Add throwable entry to standard throwable handler
    ///
    /// Note : This have to be used only in loading process and only once for single throwable,
    /// and only for existing items.
    /// If you want to add custom throwable to new item, you should attach IThrowable capability to the item.
    public static void addThrowableHandler(Predicate<ItemStack> itemMatcher, IThrowable throwable) {
        if (REGISTRY_FREEZE)
            throw new IllegalStateException("Try to register [" + throwable.getClass().getName() + "], but the registry is already freeze");
        VANILLA_THROWABLE_REGISTRY.add(() -> new VanillaThrowableEntry(itemMatcher, throwable));
    }

    /// Add throwable entry to standard throwable handler, it's added with current the highest priority
    ///
    /// Note : This have to be used only in loading process and only once for single throwable,
    /// and only for existing items.
    /// If you want to add custom throwable to new item, you should attach IThrowable capability to the item.
    public static void addThrowableHandlerHead(Predicate<ItemStack> itemMatcher, IThrowable throwable) {
        if (REGISTRY_FREEZE)
            throw new IllegalStateException("Try to register [" + throwable.getClass().getName() + "], but the registry is already freeze");
        VANILLA_THROWABLE_REGISTRY.add(0, () -> new VanillaThrowableEntry(itemMatcher, throwable));
    }

    public static StandardThrowable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StandardThrowable();
            REGISTRY_FREEZE = true;
        }
        return INSTANCE;
    }

    public boolean matchAnyEntry(ItemStack stack) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Entity throwAsEntity(Player thrower, ItemStack stack, int chargedTick) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().throwAsEntity(thrower, stack, chargedTick);
            }
        }

        Entity entity = stack.getItem().createEntity(thrower.level(), thrower, stack);
        if (entity == null) {
            return throwAsItem(thrower, stack, chargedTick);
        }
        Vec3 pos = ThrowUtil.getBasicThrowingPosition(thrower);
        Vec3 throwVec = ThrowUtil.getBasicThrowingVector(thrower);
        double speedScale = 4. * Mth.clamp(chargedTick / (double) getMaxChargeTick(stack), 0, 1);

        entity.setPos(pos.x(), pos.y() + thrower.getEyeHeight() - 0.3, pos.z());
        entity.setDeltaMovement(throwVec.scale(speedScale));

        return entity;
    }

    @Override
    public Entity throwAsItem(Player thrower, ItemStack stack, int chargedTick) {
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
    public boolean canThrowableNow(Player thrower, ItemStack stack) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                return entry.getThrowable().canThrowableNow(thrower, stack);
            }
        }
        return IThrowable.super.canThrowableNow(thrower, stack);
    }

    @Override
    public void onThrownOnClient(Player thrower, ItemStack stack, ThrowType type, int chargedTick) {
        for (VanillaThrowableEntry entry : vanillaThrowable) {
            if (entry.matches(stack)) {
                entry.getThrowable().onThrownOnClient(thrower, stack, type, chargedTick);
                return;
            }
        }
        IThrowable.super.onThrownOnClient(thrower, stack, type, chargedTick);
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
