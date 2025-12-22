package com.qtsoftwareltd.favemailapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.qtsoftwareltd.favemailapp.R
import com.qtsoftwareltd.favemailapp.ui.theme.ThemeMode

/**
 * Theme switcher dropdown menu
 * Shows in the TopAppBar to allow users to switch between light, dark, and system theme
 */
@Composable
fun ThemeSwitcher(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        // Theme icon button - changes icon based on current theme
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = when (currentTheme) {
                    ThemeMode.LIGHT -> Icons.Default.Star
                    ThemeMode.DARK -> Icons.Default.AddCircle
                    ThemeMode.SYSTEM -> Icons.Default.Settings
                },
                contentDescription = context.getString(R.string.theme_settings),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            ThemeMode.values().forEach { theme ->
                val isSelected = theme == currentTheme
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Icon for each theme option
                            Icon(
                                imageVector = when (theme) {
                                    ThemeMode.LIGHT -> Icons.Default.Star
                                    ThemeMode.DARK -> Icons.Default.AddCircle
                                    ThemeMode.SYSTEM -> Icons.Default.Settings
                                },
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            
                            // Theme name
                            Text(
                                text = when (theme) {
                                    ThemeMode.LIGHT -> context.getString(R.string.light)
                                    ThemeMode.DARK -> context.getString(R.string.dark)
                                    ThemeMode.SYSTEM -> context.getString(R.string.system)
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Checkmark for selected theme
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = context.getString(R.string.selected),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    onClick = {
                        onThemeChange(theme)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
            }
        }
    }
}

