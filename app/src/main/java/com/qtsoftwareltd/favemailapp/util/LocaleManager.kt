package com.qtsoftwareltd.favemailapp.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * Language options supported by the app
 */
enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    KINYARWANDA("rw", "Ikinyarwanda");
    
    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code == code } ?: ENGLISH
        }
    }
}

/**
 * Manages app locale/language settings
 * Allows users to manually select language (English or Kinyarwanda)
 */
object LocaleManager {
    private const val PREFS_NAME = "app_preferences"
    private const val KEY_LANGUAGE = "selected_language"
    
    /**
     * Get the currently selected language
     * Defaults to English if no preference is saved
     */
    fun getSelectedLanguage(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(KEY_LANGUAGE, null)
        return if (languageCode != null) {
            AppLanguage.fromCode(languageCode)
        } else {
            // Default to English
            AppLanguage.ENGLISH
        }
    }
    
    /**
     * Set the selected language and update app locale
     */
    fun setSelectedLanguage(context: Context, language: AppLanguage) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        updateLocale(context, language)
    }
    
    /**
     * Update the app's locale configuration
     * Returns a new context with updated locale (for Android N+)
     */
    fun updateLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        
        // For Android 7.0 (API 24) and above, use setLocale and createConfigurationContext
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        } else {
            // For older Android versions, use deprecated method
            @Suppress("DEPRECATION")
            configuration.locale = locale
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }
    
    /**
     * Get the locale for the selected language
     */
    fun getLocale(language: AppLanguage): Locale {
        return Locale(language.code)
    }
    
    /**
     * Apply the saved language to the context
     * Call this in Application.onCreate() and Activity.onCreate()
     * Returns updated context (for Android N+)
     */
    fun applySavedLanguage(context: Context): Context {
        val language = getSelectedLanguage(context)
        return updateLocale(context, language)
    }
}

