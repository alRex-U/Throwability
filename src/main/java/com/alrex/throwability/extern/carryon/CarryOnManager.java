package com.alrex.throwability.extern.carryon;

import com.alrex.throwability.extern.ExternalModManager;

// Putting off CarryOn support in version for now
// Current version of CarryOn is handle carrying objects as not item,
// This is quite different from Throwability's architecture
public class CarryOnManager extends ExternalModManager {
    public CarryOnManager() {
        super("carryon");
    }
}
