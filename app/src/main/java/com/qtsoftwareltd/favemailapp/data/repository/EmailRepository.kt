package com.qtsoftwareltd.favemailapp.data.repository

import com.qtsoftwareltd.favemailapp.data.local.EmailDao
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import com.qtsoftwareltd.favemailapp.util.HashUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for email messages
 * Handles data operations and hash verification
 */
@Singleton
class EmailRepository @Inject constructor(
    private val emailDao: EmailDao
) {
    
    /**
     * Gets the latest email message as a Flow
     */
    fun getLatestEmail(): Flow<EmailMessage?> = emailDao.getLatestEmail()
    
    /**
     * Saves an email message to the encrypted database
     * Verifies hash integrity before saving
     * 
     * @param email The email message to save
     * @return The saved email with verification status
     */
    suspend fun saveEmail(email: EmailMessage): EmailMessage {
        // Verify body hash
        val computedBodyHash = HashUtil.computeSha256Hash(email.body)
        val bodyHashVerified = computedBodyHash.equals(email.bodyHash, ignoreCase = true)
        
        // Verify image hash
        val computedImageHash = HashUtil.computeSha256Hash(email.attachedImageBytes)
        val imageHashVerified = computedImageHash.equals(email.imageHash, ignoreCase = true)
        
        // Create email with verification status
        val verifiedEmail = email.copy(
            bodyHashVerified = bodyHashVerified,
            imageHashVerified = imageHashVerified
        )
        
        // Save to encrypted database
        emailDao.insertEmail(verifiedEmail)
        
        return verifiedEmail
    }
    
    /**
     * Check if both body and image hashes are verified
     */
    fun isEmailVerified(email: EmailMessage): Boolean {
        return email.bodyHashVerified && email.imageHashVerified
    }
}

