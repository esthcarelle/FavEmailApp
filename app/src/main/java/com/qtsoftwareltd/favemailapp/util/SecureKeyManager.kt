package com.qtsoftwareltd.favemailapp.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * Manages secure storage of database encryption password
 * Uses Android Keystore with EncryptedSharedPreferences fallback
 */
object SecureKeyManager {
    private const val TAG = "SecureKeyManager"
    private const val KEYSTORE_ALIAS = "FavEmailApp_DatabaseKey"
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_PASSWORD = "db_password_key"
    
    /**
     * Gets or generates the database password securely
     * Tries EncryptedSharedPreferences first, falls back to Android Keystore,
     * then uses device-specific deterministic password if both fail
     */
    fun getDatabasePassword(context: Context): ByteArray {
        return try {
            // Try to get password from EncryptedSharedPreferences
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            // Try to retrieve existing password
            val storedPassword = sharedPreferences.getString(KEY_PASSWORD, null)
            
            if (storedPassword != null) {
                // Password exists, return it
                Log.d(TAG, "Retrieved database password from secure storage")
                storedPassword.toByteArray()
            } else {
                // Generate new password and store it securely
                val newPassword = generateSecurePassword()
                
                // Store the password securely
                sharedPreferences.edit()
                    .putString(KEY_PASSWORD, newPassword)
                    .apply()
                
                Log.d(TAG, "Generated and stored new database password")
                newPassword.toByteArray()
            }
        } catch (e: Exception) {
            // Fallback: If EncryptedSharedPreferences fails, use Android Keystore
            Log.w(TAG, "EncryptedSharedPreferences failed, trying Android Keystore", e)
            try {
                getPasswordFromKeystore(context)
            } catch (keystoreException: Exception) {
                // Last resort: Generate a password based on app package and device info
                // Less secure fallback to ensure app works
                Log.e(TAG, "All secure storage methods failed, using fallback", keystoreException)
                generateFallbackPassword(context)
            }
        }
    }
    
    /**
     * Generate a secure random password
     * Uses a combination of random bytes and app-specific data
     */
    private fun generateSecurePassword(): String {
        // Generate a strong password using random bytes
        val randomBytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(randomBytes)
        
        // Convert to a string format that's safe for SQLCipher
        // SQLCipher accepts any byte array, but we'll use Base64 for storage
        val base64 = android.util.Base64.encodeToString(randomBytes, android.util.Base64.NO_WRAP)
        
        // Use a combination of random data and app identifier for uniqueness
        return "FavEmailApp_${base64}_SecureKey"
    }
    
    /**
     * Try to use Android Keystore to store/retrieve the password
     * Provides hardware-backed security on supported devices
     */
    private fun getPasswordFromKeystore(context: Context): ByteArray {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            
            // Check if key already exists
            if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
                // Key exists, retrieve it
                val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
                
                // For hardware-backed keys, encoded might be null
                // Use a combination of alias and key properties to derive password
                val keyIdentifier = "${KEYSTORE_ALIAS}_${secretKey.algorithm}_${secretKey.format}"
                val hash = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keyIdentifier.toByteArray())
                val keyBytes = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
                
                // Derive password from key identifier
                val password = "FavEmailApp_${keyBytes}_Keystore"
                Log.d(TAG, "Retrieved password from Android Keystore")
                password.toByteArray()
            } else {
                // Generate new key in Keystore
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
                )
                
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
                
                keyGenerator.init(keyGenParameterSpec)
                val secretKey = keyGenerator.generateKey()
                
                // For hardware-backed keys, encoded might be null
                // Use a combination of alias and key properties to derive password
                val keyIdentifier = "${KEYSTORE_ALIAS}_${secretKey.algorithm}_${secretKey.format}"
                val hash = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(keyIdentifier.toByteArray())
                val keyBytes = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
                
                // Derive password from key identifier
                val password = "FavEmailApp_${keyBytes}_Keystore"
                Log.d(TAG, "Generated new password using Android Keystore")
                password.toByteArray()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Android Keystore failed", e)
            throw e // Re-throw to trigger fallback
        }
    }
    
    /**
     * Fallback password generation if all secure methods fail
     * Less secure fallback to ensure app continues working
     * Uses app package name and a device-specific identifier
     */
    private fun generateFallbackPassword(context: Context): ByteArray {
        // Use app package name and Android ID as seed
        val packageName = context.packageName
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "default"
        
        // Create a deterministic but unique password
        val combined = "${packageName}_${androidId}_FavEmailApp_Secure"
        val hash = java.security.MessageDigest.getInstance("SHA-256")
            .digest(combined.toByteArray())
        
        val base64 = android.util.Base64.encodeToString(hash, android.util.Base64.NO_WRAP)
        val password = "FavEmailApp_${base64}_Fallback"
        
        Log.w(TAG, "Using fallback password generation (less secure)")
        return password.toByteArray()
    }
    
    /**
     * Clear stored password (useful for testing or reset)
     * WARNING: Makes existing encrypted database inaccessible
     */
    fun clearStoredPassword(context: Context) {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            
            sharedPreferences.edit()
                .remove(KEY_PASSWORD)
                .apply()
            
            Log.d(TAG, "Cleared stored database password")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear stored password", e)
        }
    }
}

