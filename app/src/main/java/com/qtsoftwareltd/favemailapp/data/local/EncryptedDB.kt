package com.qtsoftwareltd.favemailapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import com.qtsoftwareltd.favemailapp.util.SecureKeyManager
import net.sqlcipher.database.SupportFactory
import java.io.File

/**
 * Encrypted database using Room and SQLCipher
 * All database data is encrypted at rest
 * 
 * The database password is securely stored using:
 * - Android Keystore (hardware-backed security when available)
 * - EncryptedSharedPreferences (software-based encryption as fallback)
 * 
 * The password is never stored in plain text in the code
 */
@Database(
    entities = [EmailMessage::class],
    version = 1,
    exportSchema = false
)
abstract class EncryptedDB : RoomDatabase() {
    
    abstract fun emailDao(): EmailDao
    
    companion object {
        private const val TAG = "EncryptedDB"
        private const val DATABASE_NAME = "encrypted_email_db"
        
        /**
         * Create an instance of the encrypted database
         * SQLCipher encrypts all data at rest
         * 
         * The database password is retrieved securely from SecureKeyManager,
         * which uses Android Keystore and EncryptedSharedPreferences
         * 
         * If the database was created with a different password (e.g., old hardcoded password),
         * deletes the old database and creates a new one with the secure password.
         */
        fun create(context: Context): EncryptedDB {
            // Get database password securely from SecureKeyManager
            // This uses Android Keystore or EncryptedSharedPreferences
            val passwordBytes = SecureKeyManager.getDatabasePassword(context)
            
            // Create SQLCipher factory for encryption
            // SQLCipher 4.x uses SupportFactory with password as ByteArray
            val factory = SupportFactory(passwordBytes)
            
            val databaseBuilder = Room.databaseBuilder(
                context.applicationContext,
                EncryptedDB::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration() // For development - remove in production
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.d(TAG, "Database opened successfully")
                    }
                })
            
            // Build the database
            // Note: Room's build() is lazy, so the database is only opened when first accessed
            // If there's a password mismatch, it will throw SQLiteException on first access
            val database = databaseBuilder.build()
            
            // Try to open the database immediately to catch password mismatch errors early
            try {
                // Force database initialization by accessing it
                database.openHelper.writableDatabase
            } catch (e: SQLiteException) {
                // Check if the error is due to password mismatch (old database with different password)
                if (e.message?.contains("file is not a database") == true ||
                    e.message?.contains("not a database") == true ||
                    e.message?.contains("SQLiteException") == true) {
                    Log.w(TAG, "Database password mismatch detected. Deleting old database and creating new one.", e)
                    
                    // Close the database connection if it was partially opened
                    try {
                        database.close()
                    } catch (closeException: Exception) {
                        Log.w(TAG, "Error closing database", closeException)
                    }
                    
                    // Delete the old database files
                    deleteDatabaseFiles(context, DATABASE_NAME)
                    
                    // Create a new database builder and build again
                    return Room.databaseBuilder(
                        context.applicationContext,
                        EncryptedDB::class.java,
                        DATABASE_NAME
                    )
                        .openHelperFactory(factory)
                        .fallbackToDestructiveMigration()
                        .build()
                } else {
                    // Re-throw if it's a different error
                    throw e
                }
            }
            
            return database
        }
        
        /**
         * Delete all database files (database, journal, wal, shm files)
         * Used when the database password has changed
         */
        private fun deleteDatabaseFiles(context: Context, databaseName: String) {
            val databasePath = context.getDatabasePath(databaseName)
            val databaseDir = databasePath.parentFile
            
            // List of possible database file extensions
            val fileExtensions = listOf("", "-journal", "-wal", "-shm")
            
            fileExtensions.forEach { extension ->
                val file = File(databaseDir, "$databaseName$extension")
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d(TAG, "Deleted database file: ${file.name}, success: $deleted")
                }
            }
        }
    }
}

