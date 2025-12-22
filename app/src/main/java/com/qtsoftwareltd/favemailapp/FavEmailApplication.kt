package com.qtsoftwareltd.favemailapp

import android.app.Application
import com.qtsoftwareltd.favemailapp.util.LocaleManager
import dagger.hilt.android.HiltAndroidApp
import net.sqlcipher.database.SQLiteDatabase

/**
 * Application class for Hilt dependency injection
 * This must be annotated with @HiltAndroidApp for Hilt to work
 */
@HiltAndroidApp
class FavEmailApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Apply saved language preference before any UI is created
        LocaleManager.applySavedLanguage(this)
        
        // Initialize SQLCipher native libraries
        // This must be called before any database operations
        SQLiteDatabase.loadLibs(this)
    }
    
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reapply language when configuration changes
        LocaleManager.applySavedLanguage(this)
    }
}

