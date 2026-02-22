package com.alrex.throwability.extern;

import net.minecraftforge.fml.ModList;

public abstract class ExternalModManager {
    private final String modID;
    private boolean installed = false;

    public ExternalModManager(String modID) {
        this.modID = modID;
    }

    public void init() {
        installed = ModList.get().getModFileById(modID) != null;
    }

    public boolean isInstalled() {
        return installed;
    }

    public String getModID() {
        return modID;
    }
}
