package com.qtsoftwareltd.favemailapp.data.error

import android.util.Log
import com.google.protobuf.InvalidProtocolBufferException
import java.io.IOException

/**
 * Maps exceptions to AppError types
 * This centralizes error handling logic and makes it easier to maintain
 */
object ErrorMapper {
    private const val TAG = "ErrorMapper"
    
    /**
     * Map an exception to an AppError
     * Converts exceptions to AppError types
     */
    fun mapException(exception: Throwable): AppError {
        return when (exception) {
            is InvalidProtocolBufferException -> {
                Log.e(TAG, "Invalid protobuf format", exception)
                AppError.FileError.InvalidFormat
            }
            is IOException -> {
                Log.e(TAG, "IO error", exception)
                AppError.FileError.CannotRead
            }
            is AppErrorException -> {
                // Custom exception that already contains AppError
                exception.appError
            }
            else -> {
                // Try to map based on exception message
                mapByMessage(exception.message ?: "")
            }
        }
    }
    
    /**
     * Map exception message to AppError
     * This handles cases where we throw generic Exception with specific messages
     */
    private fun mapByMessage(message: String): AppError {
        return when {
            message.startsWith("The selected file is empty") -> 
                AppError.FileError.Empty
            message.startsWith("The selected file appears to be empty") -> 
                AppError.FileError.Corrupted
            message.startsWith("The file appears to be a valid protobuf") -> 
                AppError.FileError.NoEmailData
            message.startsWith("The selected file is not a valid email file") -> 
                AppError.FileError.InvalidFormat
            message.startsWith("Could not read the selected file") -> 
                AppError.FileError.CannotRead
            message.startsWith("Failed to parse email file") -> 
                AppError.FileError.ParseFailed
            message.startsWith("File does not exist") -> 
                AppError.FileError.NotFound
            message.startsWith("File is not readable") -> 
                AppError.FileError.NotReadable
            else -> 
                AppError.Unknown(originalMessage = message)
        }
    }
    
    /**
     * Wrap an exception in a Result.Error
     */
    fun <T> mapToResult(exception: Throwable): Result<T> {
        return Result.Error(mapException(exception))
    }
}

/**
 * Custom exception that carries an AppError
 * Useful when you want to throw an exception but preserve the error type
 */
class AppErrorException(val appError: AppError) : Exception(appError.toString())

