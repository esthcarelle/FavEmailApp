package com.qtsoftwareltd.favemailapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.qtsoftwareltd.favemailapp.ui.theme.ThemeMode
import com.qtsoftwareltd.favemailapp.util.AppLanguage
import com.qtsoftwareltd.favemailapp.util.LocaleManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.qtsoftwareltd.favemailapp.ui.EmailScreen
import com.qtsoftwareltd.favemailapp.ui.rememberEmailAppState
import com.qtsoftwareltd.favemailapp.ui.theme.FavEmailAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Main activity of the application
 * Handles file picker and permissions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // File picker launcher - opens system file picker
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Handle the selected file
            handleSelectedFile(selectedUri)
        }
    }
    
    // Permission launcher for older Android versions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, open file picker
            openFilePicker()
        } else {
            // Permission denied, show message
            showError("Storage permission is required to select email files")
        }
    }
    
    // Callback to notify Compose when a file is selected
    private var fileSelectedCallback: ((File) -> Unit)? = null
    
    // Track if this is the first launch (not a recreation)
    private var isFirstLaunch = true
    
    override fun attachBaseContext(newBase: Context) {
        // Apply saved language before creating the context
        // Sets locale before resources are loaded
        val updatedContext = LocaleManager.applySavedLanguage(newBase)
        super.attachBaseContext(updatedContext)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if this is a recreation (savedInstanceState is not null)
        isFirstLaunch = savedInstanceState == null
        
        enableEdgeToEdge()
        
        setContent {
            // Get current language from LocaleManager
            val currentLanguage = LocaleManager.getSelectedLanguage(this@MainActivity)
            
            // Hoist all app state to a single state holder
            val appState = rememberEmailAppState(
                initialTheme = ThemeMode.SYSTEM,
                initialLanguage = currentLanguage,
                onSelectFileRequested = {
                    // Check permissions before opening file picker
                    checkPermissionsAndOpenPicker()
                },
                onLanguageChange = { newLanguage ->
                    // Save language preference and restart activity to apply changes
                    LocaleManager.setSelectedLanguage(this@MainActivity, newLanguage)
                    recreate() // Restart activity to apply new locale
                }
            )
            
            // Store callback reference so handleSelectedFile can use it
            fileSelectedCallback = { file ->
                appState.selectFile(file)
            }
            
            // Access theme mode value using by delegate for cleaner syntax
            val currentTheme by appState.themeMode
            
            FavEmailAppTheme(themeMode = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmailScreen(
                        appState = appState
                    )
                }
            }
        }
        
        // Only automatically open file picker on first launch, not on recreation
        if (isFirstLaunch) {
            lifecycleScope.launch {
                checkPermissionsAndOpenPicker()
            }
        }
    }
    
    /**
     * Check if we have necessary permissions and open file picker
     * For Android 13+ (API 33+), we don't need storage permission for file picker
     * For older versions, we request READ_EXTERNAL_STORAGE permission
     */
    private fun checkPermissionsAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - no permission needed for file picker
            openFilePicker()
        } else {
            // Older Android versions - check for READ_EXTERNAL_STORAGE
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    openFilePicker()
                }
                else -> {
                    // Request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
    
    /**
     * Open the system file picker
     * Filters to show only .pb files
     */
    private fun openFilePicker() {
        filePickerLauncher.launch("*/*") // Accept any file type
    }
    
    /**
     * Handle the selected file URI
     * Reads the file and stores it for the ViewModel to access
     */
    private fun handleSelectedFile(uri: Uri) {
        try {
            // Get file input stream from URI
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                showError("Could not open selected file")
                return
            }
            
            // Create a temporary file with unique name to ensure LaunchedEffect triggers
            val timestamp = System.currentTimeMillis()
            val tempFile = File(cacheDir, "temp_email_$timestamp.pb")
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            
            // Clean up old temp files (keep only the latest)
            cacheDir.listFiles()?.filter { it.name.startsWith("temp_email_") && it.name.endsWith(".pb") }
                ?.sortedByDescending { it.lastModified() }
                ?.drop(1) // Keep the latest one
                ?.forEach { it.delete() }
            
            // Notify Compose about the file selection via callback
            fileSelectedCallback?.invoke(tempFile)
            
            android.util.Log.d("MainActivity", "File selected: ${tempFile.absolutePath}, size: ${tempFile.length()} bytes")
            
        } catch (e: Exception) {
            showError("Error reading file: ${e.message}")
            android.util.Log.e("MainActivity", "Error handling file", e)
        }
    }
    
    /**
     * Show error message to user
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
