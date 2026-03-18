package com.alrex.throwability.extern;

import com.alrex.throwability.extern.carryon.CarryOnManager;
import com.alrex.throwability.extern.naturot.NaturotManager;

import java.util.Arrays;

public class AdditionalMods {
    private static final ExternalModManager[] modManagers = new ExternalModManager[]{
            new NaturotManager(),
            new CarryOnManager()
    };

    public static NaturotManager Naturot() {
        return (NaturotManager) modManagers[0];
    }

    public static CarryOnManager CarryOn() {
        return (CarryOnManager) modManagers[1];
    }

    public static void init() {
        Arrays.stream(modManagers).forEach(ExternalModManager::init);
    }
}

