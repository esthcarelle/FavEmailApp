package com.qtsoftwareltd.favemailapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.qtsoftwareltd.favemailapp.ui.theme.ThemeMode
import com.qtsoftwareltd.favemailapp.util.AppLanguage
import java.io.File

/**
 * State holder for the email app
 * Hoists UI state to a single place for easier management
 */
@Stable
class EmailAppState(
    // Theme state
    val themeMode: MutableState<ThemeMode>,
    val onThemeChange: (ThemeMode) -> Unit,
    
    // Language state
    val language: MutableState<AppLanguage>,
    val onLanguageChange: (AppLanguage) -> Unit,
    
    // File selection state
    val selectedFile: MutableState<File?>,
    val onFileSelected: (File) -> Unit,
    val onSelectFileRequested: () -> Unit
) {
    /**
     * Update the selected file
     * Called when a file is selected from the file picker
     */
    fun selectFile(file: File) {
        selectedFile.value = file
    }
    
    /**
     * Clear the selected file
     * Useful for resetting state
     */
    fun clearSelectedFile() {
        selectedFile.value = null
    }
}

/**
 * Remember and create the EmailAppState
 * State hoisting function for app-level state management
 * 
 * @param initialTheme The initial theme mode (defaults to SYSTEM)
 * @param initialLanguage The initial language (defaults to system language)
 * @param onSelectFileRequested Callback when user requests to select a file
 * @param onLanguageChange Callback when language changes (to restart activity)
 * @return EmailAppState instance with all hoisted state
 */
@Composable
fun rememberEmailAppState(
    initialTheme: ThemeMode = ThemeMode.SYSTEM,
    initialLanguage: AppLanguage,
    onSelectFileRequested: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
): EmailAppState {
    // Hoist theme state - explicitly type as MutableState
    val themeMode: MutableState<ThemeMode> = remember { mutableStateOf(initialTheme) }
    
    // Hoist language state - explicitly type as MutableState
    val language: MutableState<AppLanguage> = remember { mutableStateOf(initialLanguage) }
    
    // Hoist file selection state - explicitly type as MutableState
    val selectedFile: MutableState<File?> = remember { mutableStateOf<File?>(null) }
    
    return remember(themeMode, language, selectedFile, onSelectFileRequested, onLanguageChange) {
        EmailAppState(
            themeMode = themeMode,
            onThemeChange = { newTheme -> themeMode.value = newTheme },
            language = language,
            onLanguageChange = { newLanguage ->
                language.value = newLanguage
                onLanguageChange(newLanguage)
            },
            selectedFile = selectedFile,
            onFileSelected = { file -> selectedFile.value = file },
            onSelectFileRequested = onSelectFileRequested
        )
    }
}

