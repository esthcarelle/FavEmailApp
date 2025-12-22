package com.qtsoftwareltd.favemailapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for email messages
 * Provides methods to interact with the encrypted database
 */
@Dao
interface EmailDao {
    
    /**
     * Insert or update an email message in the database
     * If an email with the same ID exists, it will be replaced
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailMessage): Long
    
    /**
     * Get the most recent email message as a Flow
     * Flow automatically updates when the database changes
     */
    @Query("SELECT * FROM email_messages ORDER BY createdAt DESC LIMIT 1")
    fun getLatestEmail(): Flow<EmailMessage?>
    
    /**
     * Get all email messages as a Flow
     */
    @Query("SELECT * FROM email_messages ORDER BY createdAt DESC")
    fun getAllEmails(): Flow<List<EmailMessage>>
    
    /**
     * Delete all email messages from the database
     */
    @Query("DELETE FROM email_messages")
    suspend fun deleteAllEmails()
}

