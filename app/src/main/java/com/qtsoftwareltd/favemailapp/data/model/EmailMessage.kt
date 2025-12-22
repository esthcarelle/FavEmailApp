package com.qtsoftwareltd.favemailapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Email message data model stored in the encrypted database
 * This represents a parsed email message with verification status
 */
@Entity(tableName = "email_messages")
data class EmailMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderEmailAddress: String,
    val subject: String,
    val body: String,
    val attachedImageBytes: ByteArray,
    val bodyHash: String,
    val imageHash: String,
    val bodyHashVerified: Boolean = false,
    val imageHashVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Override equals to compare ByteArray properly
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmailMessage

        if (id != other.id) return false
        if (senderName != other.senderName) return false
        if (senderEmailAddress != other.senderEmailAddress) return false
        if (subject != other.subject) return false
        if (body != other.body) return false
        if (!attachedImageBytes.contentEquals(other.attachedImageBytes)) return false
        if (bodyHash != other.bodyHash) return false
        if (imageHash != other.imageHash) return false
        if (bodyHashVerified != other.bodyHashVerified) return false
        if (imageHashVerified != other.imageHashVerified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + senderEmailAddress.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + attachedImageBytes.contentHashCode()
        result = 31 * result + bodyHash.hashCode()
        result = 31 * result + imageHash.hashCode()
        result = 31 * result + bodyHashVerified.hashCode()
        result = 31 * result + imageHashVerified.hashCode()
        return result
    }
}

