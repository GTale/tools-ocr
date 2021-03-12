package com.luooqi.ocr.utils;

import java.util.prefs.Preferences;

public class PrefsSingleton {
    private static Preferences instance = null;

    public synchronized static Preferences get() {
        if (instance == null) {
            instance = Preferences.userNodeForPackage(com.luooqi.ocr.MainFm.class);
        }
        return instance;
    }
}
