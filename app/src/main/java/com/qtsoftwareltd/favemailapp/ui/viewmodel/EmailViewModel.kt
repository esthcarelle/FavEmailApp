package com.qtsoftwareltd.favemailapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qtsoftwareltd.favemailapp.R
import com.qtsoftwareltd.favemailapp.data.error.AppError
import com.qtsoftwareltd.favemailapp.data.error.AppErrorException
import com.qtsoftwareltd.favemailapp.data.error.ErrorMapper
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import com.qtsoftwareltd.favemailapp.data.parser.ProtobufParser
import com.qtsoftwareltd.favemailapp.data.repository.EmailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for email display screen
 * Manages UI state and handles business logic
 * Survives configuration changes (screen rotation, etc.)
 */
@HiltViewModel
class EmailViewModel @Inject constructor(
    application: Application,
    private val emailRepository: EmailRepository
) : AndroidViewModel(application) {
    
    // UI state sealed class for different states
    sealed class EmailUiState {
        object Initial : EmailUiState()                    // No file selected yet
        object Loading : EmailUiState()                    // Loading/parsing file
        data class Success(val email: EmailMessage) : EmailUiState()  // Email loaded successfully
        data class Error(val message: String) : EmailUiState()        // Error occurred
    }
    
    // Current UI state - observable by the UI
    private val _uiState = MutableStateFlow<EmailUiState>(EmailUiState.Initial)
    val uiState: StateFlow<EmailUiState> = _uiState.asStateFlow()
    
    // Current email message - observable by the UI
    private val _email = MutableStateFlow<EmailMessage?>(null)
    val email: StateFlow<EmailMessage?> = _email.asStateFlow()
    
    init {
        // Observe the latest email from the database
        // Observes database changes and updates UI
        viewModelScope.launch {
            emailRepository.getLatestEmail()
                .catch { exception ->
                    // Only show error if we're not currently loading a file
                    if (_uiState.value !is EmailUiState.Loading) {
                        val errorMsg = getApplication<Application>().getString(
                            R.string.error_loading_email,
                            exception.message ?: ""
                        )
                        _uiState.value = EmailUiState.Error(errorMsg)
                    }
                }
                .collect { emailMessage ->
                    emailMessage?.let {
                        _email.value = it
                        // Always update to Success when we receive an email from the database
                        // State updates after loadEmailFile saves the email
                        _uiState.value = EmailUiState.Success(it)
                    } ?: run {
                        // If email is null and we're not loading, go to Initial state
                        if (_uiState.value !is EmailUiState.Loading) {
                            _uiState.value = EmailUiState.Initial
                        }
                    }
                }
        }
    }
    
    /**
     * Load and parse an email file from the selected file URI
     * 
     * @param file The .pb file to load
     */
    fun loadEmailFile(file: File) {
        viewModelScope.launch {
            try {
                // Show loading state
                _uiState.value = EmailUiState.Loading
                
                android.util.Log.d("EmailViewModel", "Loading file: ${file.absolutePath}, exists: ${file.exists()}, size: ${file.length()}")
                
                // Check if file exists
                if (!file.exists()) {
                    throw Exception("File does not exist: ${file.absolutePath}")
                }
                
                // Check if file is readable
                if (!file.canRead()) {
                    throw Exception("File is not readable: ${file.absolutePath}")
                }
                
                // Validate file before parsing
                val validationResult = validateFile(file)
                if (validationResult is com.qtsoftwareltd.favemailapp.data.error.Result.Error) {
                    handleError(validationResult.error)
                    return@launch
                }
                
                // Parse the Protocol Buffer file using Result pattern
                when (val parseResult = ProtobufParser.parseEmailFile(file)) {
                    is com.qtsoftwareltd.favemailapp.data.error.Result.Success -> {
                        val emailMessage = parseResult.data
                        android.util.Log.d("EmailViewModel", "Parsed email: ${emailMessage.subject}")
                        
                        // Save to encrypted database (this also verifies hashes)
                        try {
                            emailRepository.saveEmail(emailMessage)
                            android.util.Log.d("EmailViewModel", "Email saved to database")
                            
                            // The state will be updated automatically via the Flow observer in init block
                            // The Flow will emit the new email and update the UI state to Success
                        } catch (e: Exception) {
                            android.util.Log.e("EmailViewModel", "Error saving email to database", e)
                            handleError(ErrorMapper.mapException(e))
                        }
                    }
                    is com.qtsoftwareltd.favemailapp.data.error.Result.Error -> {
                        handleError(parseResult.error)
                    }
                }
                
            } catch (e: Exception) {
                // Fallback for any unexpected exceptions
                android.util.Log.e("EmailViewModel", "Unexpected error loading file", e)
                handleError(ErrorMapper.mapException(e))
            }
        }
    }
    
    /**
     * Check if the current email is fully verified
     */
    fun isEmailVerified(): Boolean {
        return _email.value?.let { emailRepository.isEmailVerified(it) } ?: false
    }
    
    /**
     * Validate file before parsing
     * Returns Result for type-safe error handling
     */
    private fun validateFile(file: File): com.qtsoftwareltd.favemailapp.data.error.Result<File> {
        return when {
            !file.exists() -> {
                com.qtsoftwareltd.favemailapp.data.error.Result.Error(AppError.FileError.NotFound)
            }
            !file.canRead() -> {
                com.qtsoftwareltd.favemailapp.data.error.Result.Error(AppError.FileError.NotReadable)
            }
            else -> {
                com.qtsoftwareltd.favemailapp.data.error.Result.Success(file)
            }
        }
    }
    
    /**
     * Handle error by converting AppError to user-friendly message and updating UI state
     * This centralizes error handling logic
     */
    private fun handleError(error: AppError) {
        android.util.Log.e("EmailViewModel", "Error: ${error.javaClass.simpleName}")
        val errorMessage = error.getMessage(getApplication<Application>())
        _uiState.value = EmailUiState.Error(errorMessage)
    }
}

