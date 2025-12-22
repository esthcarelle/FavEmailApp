package com.qtsoftwareltd.favemailapp.data.error

/**
 * Result pattern for handling success and error states
 * This is a type-safe way to handle operations that can fail
 * 
 * Usage:
 * ```
 * when (val result = someOperation()) {
 *     is Result.Success -> handleSuccess(result.data)
 *     is Result.Error -> handleError(result.error)
 * }
 * ```
 */
sealed class Result<out T> {
    /**
     * Success case - contains the data
     */
    data class Success<T>(val data: T) : Result<T>()
    
    /**
     * Error case - contains the error information
     */
    data class Error(val error: AppError) : Result<Nothing>()
    
    /**
     * Check if result is success
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * Check if result is error
     */
    fun isError(): Boolean = this is Error
    
    /**
     * Get data if success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    /**
     * Get data if success, or throw exception
     * Note: Requires context for error message, use getOrNull() and handle error separately
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw Exception("Error: ${error.javaClass.simpleName}")
    }
    
    /**
     * Map the success value
     */
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
    
    /**
     * Map the error value
     */
    fun mapError(transform: (AppError) -> AppError): Result<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(error))
    }
}

/**
 * Extension function to convert Result to nullable
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
}

/**
 * Extension function to get data or default value
 */
fun <T> Result<T>.getOrDefault(default: T): T = when (this) {
    is Result.Success -> data
    is Result.Error -> default
}

/**
 * Helper function to create success result
 */
fun <T> success(data: T): Result<T> = Result.Success(data)

/**
 * Helper function to create error result
 */
fun <T> error(error: AppError): Result<T> = Result.Error(error)

