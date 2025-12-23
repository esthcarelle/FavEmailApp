package com.qtsoftwareltd.favemailapp.data.error

import android.content.Context
import com.qtsoftwareltd.favemailapp.R

/**
 * Sealed class representing all possible errors in the app
 * Type-safe error handling with sealed classes
 */
sealed class AppError(
    messageArgs: Array<String> = emptyArray()
) {
    /**
     * Abstract property for the string resource ID
     * Each error type must provide its own message resource ID
     */
    abstract val messageResId: Int
    
    /**
     * Optional message arguments for formatted strings
     */
    val messageArgs: Array<String> = messageArgs
    
    /**
     * Get user-friendly error message
     * Can be overridden by subclasses for custom message formatting
     */
    open fun getMessage(context: Context): String {
        return if (messageArgs.isEmpty()) {
            context.getString(messageResId)
        } else {
            context.getString(messageResId, *messageArgs)
        }
    }
    
    /**
     * Convert to Exception for backward compatibility
     * Note: This requires a Context, so use getMessage(context) instead
     */
    fun toException(context: Context): Exception = Exception(getMessage(context))
    
    /**
     * File-related errors
     */
    sealed class FileError : AppError() {
        object Empty : FileError() {
            override val messageResId = R.string.error_file_empty
        }
        
        object Corrupted : FileError() {
            override val messageResId = R.string.error_file_corrupted
        }
        
        object NotFound : FileError() {
            override val messageResId = R.string.error_file_not_found
        }
        
        object NotReadable : FileError() {
            override val messageResId = R.string.error_file_not_readable
        }
        
        object InvalidFormat : FileError() {
            override val messageResId = R.string.error_file_invalid_format
        }
        
        object CannotRead : FileError() {
            override val messageResId = R.string.error_file_cannot_read
        }
        
        object NoEmailData : FileError() {
            override val messageResId = R.string.error_file_no_email_data
        }
        
        object ParseFailed : FileError() {
            override val messageResId = R.string.error_file_parse_failed
        }
    }
    
    /**
     * Database-related errors
     */
    sealed class DatabaseError : AppError() {
        object SaveFailed : DatabaseError() {
            override val messageResId = R.string.error_load_email_generic
        }
        object LoadFailed : DatabaseError() {
            override val messageResId = R.string.error_loading_email
        }
    }
    
    /**
     * Network-related errors (for future use)
     */
    sealed class NetworkError : AppError() {
        object ConnectionFailed : NetworkError() {
            override val messageResId = R.string.error_load_email_generic
        }
        object Timeout : NetworkError() {
            override val messageResId = R.string.error_load_email_generic
        }
    }
    
    /**
     * Unknown/Generic error
     */
    data class Unknown(
        val originalMessage: String? = null
    ) : AppError() {
        override val messageResId = R.string.error_load_email_generic
        
        override fun getMessage(context: Context): String {
            return if (originalMessage != null) {
                context.getString(R.string.error_load_email_with_message, originalMessage)
            } else {
                super.getMessage(context)
            }
        }
    }
}

