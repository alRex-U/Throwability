package com.alrex.throwability.client.animation;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Animations {
    private static final ArrayList<Entry<?>> animationRegistry = new ArrayList<>();
    private static final ArrayList<Entry<?>> independentAnimationRegistry = new ArrayList<>();

    public static AnimationHost newAnimationHost() {
        return new AnimationHost(
                animationRegistry.stream().map(it -> it.getSupplier().get()).collect(Collectors.toList()),
                independentAnimationRegistry.stream().map(it -> it.getSupplier().get()).collect(Collectors.toList())
        );
    }

    private static int getRegisteredEntryIndex(Class<? extends IAnimation> clazz, ArrayList<Entry<?>> registry) {
        for (int i = 0; i < registry.size(); i++) {
            Entry<?> entry = registry.get(i);
            if (entry.getClazz() == clazz) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends IAnimation> void registerIndependently(Class<T> clazz, Supplier<T> supplier) {
        int idx = getRegisteredEntryIndex(clazz, independentAnimationRegistry);
        if (idx >= 0) {
            throw new IllegalStateException("The animation[" + clazz.getSimpleName() + "] is already registered");
        }
        animationRegistry.add(new Entry<>(clazz, supplier));
    }

    public static <T extends IAnimation> void register(Class<T> clazz, Supplier<T> supplier) {
        int idx = getRegisteredEntryIndex(clazz, animationRegistry);
        if (idx >= 0) {
            throw new IllegalStateException("The animation[" + clazz.getSimpleName() + "] is already registered");
        }
        animationRegistry.add(new Entry<>(clazz, supplier));
    }

    public static <T extends IAnimation> void registerAfter(Class<T> clazz, Supplier<T> supplier, Class<? extends IAnimation> base) {
        int idx = getRegisteredEntryIndex(clazz, animationRegistry);
        if (idx >= 0) {
            throw new IllegalStateException("The animation[" + clazz.getSimpleName() + "] is already registered");
        }
        int baseIdx = getRegisteredEntryIndex(base, animationRegistry);
        if (baseIdx < 0) {
            throw new IllegalStateException("The base animation[" + base.getSimpleName() + "] is not registered");
        }
        animationRegistry.add(baseIdx + 1, new Entry<>(clazz, supplier));
    }

    public static <T extends IAnimation> void registerBefore(Class<T> clazz, Supplier<T> supplier, Class<? extends IAnimation> base) {
        int idx = getRegisteredEntryIndex(clazz, animationRegistry);
        if (idx >= 0) {
            throw new IllegalStateException("The animation[" + clazz.getSimpleName() + "] is already registered");
        }
        int baseIdx = getRegisteredEntryIndex(base, animationRegistry);
        if (baseIdx < 0) {
            throw new IllegalStateException("The base animation[" + base.getSimpleName() + "] is not registered");
        }
        animationRegistry.add(baseIdx, new Entry<>(clazz, supplier));
    }

    private static class Entry<T extends IAnimation> {
        private final Class<T> clazz;
        private final Supplier<T> supplier;

        public Entry(Class<T> clazz, Supplier<T> supplier) {
            this.clazz = clazz;
            this.supplier = supplier;
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public Supplier<T> getSupplier() {
            return supplier;
        }
    }
}
