# FavEmailApp - Email Viewer with Protocol Buffers

An Android application that loads and displays email messages stored in Protocol Buffer format, with hash verification for data integrity. Built with modern Android development practices including Jetpack Compose, MVVM architecture, and secure data storage.

## Features

### Core Functionality
- ✅ **Protocol Buffer Support**: Loads and parses `.pb` files containing email messages
- ✅ **Hash Verification**: Verifies SHA-256 hashes for email body and attached images with visual badges
- ✅ **Encrypted Database**: Stores email data in an encrypted SQLCipher database
- ✅ **File Picker Integration**: System file picker for selecting email files
- ✅ **Multiple File Support**: Load and view different email files seamlessly

### Architecture & Code Quality
- ✅ **MVVM Architecture**: Clean separation of concerns with ViewModel and Repository
- ✅ **Dependency Injection**: Uses Hilt for dependency injection
- ✅ **Reactive Programming**: Kotlin Coroutines and Flow for asynchronous operations
- ✅ **State Management**: StateFlow and Flow for reactive UI updates
- ✅ **State Preservation**: Data persists across configuration changes
- ✅ **Error Handling**: Result pattern with typed error hierarchy for robust error management

### UI/UX Features
- ✅ **Jetpack Compose**: Modern declarative UI built with Compose
- ✅ **Shimmer Loading Effect**: Beautiful animated loading placeholders that mimic content layout
- ✅ **Smooth Animations**: Staggered card animations and state transitions
- ✅ **Theme Support**: Light, Dark, and System theme modes with Material 3 design
- ✅ **Localization**: Support for English and Kinyarwanda (Ikinyarwanda) languages
- ✅ **Material Design 3**: Modern Material Design with custom color schemes
- ✅ **Responsive Layout**: Clean, card-based layout with proper spacing and typography

### Security
- ✅ **Database Encryption**: SQLCipher encryption for all stored data
- ✅ **Secure Key Storage**: Android Keystore and EncryptedSharedPreferences for key management
- ✅ **Permission Handling**: Proper handling of storage permissions for different Android versions

## Tech Stack

### Core Technologies
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt 2.51.1
- **Database**: Room 2.6.1 + SQLCipher 4.5.4 (encrypted)
- **Data Format**: Protocol Buffers 3.25.3
- **Async Operations**: Kotlin Coroutines 1.7.3 & Flow
- **Security**: AndroidX Security Crypto 1.1.0-alpha06

### Android Versions
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Compile SDK**: 36

## Project Structure

```
app/src/main/
├── java/com/qtsoftwareltd/favemailapp/
│   ├── data/
│   │   ├── di/                    # Hilt dependency injection modules
│   │   │   ├── DatabaseModule.kt  # Database and DAO providers
│   │   │   └── RepositoryModule.kt # Repository providers
│   │   ├── error/                 # Error handling
│   │   │   ├── AppError.kt        # Sealed class error hierarchy
│   │   │   ├── ErrorMapper.kt     # Exception to error mapping
│   │   │   └── Result.kt          # Result pattern implementation
│   │   ├── local/                 # Database layer
│   │   │   ├── EncryptedDB.kt     # Room database with SQLCipher
│   │   │   └── EmailDao.kt        # Data access object
│   │   ├── model/                 # Data models
│   │   │   └── EmailMessage.kt    # Room entity
│   │   ├── parser/                # Protocol Buffer parser
│   │   │   └── ProtobufParser.kt  # File parsing with error handling
│   │   └── repository/            # Repository layer
│   │       └── EmailRepository.kt  # Data repository with hash verification
│   ├── ui/
│   │   ├── EmailAppState.kt       # Centralized app state holder
│   │   ├── EmailScreen.kt         # Main UI screen
│   │   ├── LanguageSwitcher.kt    # Language selection component
│   │   ├── ShimmerEffect.kt       # Shimmer loading animations
│   │   ├── ThemeSwitcher.kt       # Theme selection component
│   │   ├── theme/                 # Theme configuration
│   │   │   ├── Color.kt           # Color definitions
│   │   │   └── Theme.kt           # Material 3 theme
│   │   └── viewmodel/             # ViewModels
│   │       └── EmailViewModel.kt   # Email screen ViewModel
│   ├── util/                      # Utility classes
│   │   ├── HashUtil.kt            # SHA-256 hash computation
│   │   ├── LocaleManager.kt       # Language/locale management
│   │   └── SecureKeyManager.kt    # Secure key storage
│   ├── FavEmailApplication.kt    # Application class with Hilt
│   └── MainActivity.kt            # Main activity with file picker
├── proto/
│   └── email_message.proto        # Protocol Buffer schema
└── res/                           # Resources
    ├── values/                    # English strings
    └── values-rw/                 # Kinyarwanda strings
```

## Protocol Buffer Schema

The email message is defined in `app/src/main/proto/email_message.proto`:

```protobuf
syntax = "proto3";

package com.qtsoftwareltd.favemailapp.proto;

message EmailMessage {
  string sender_name = 1;              // Name of the email sender
  string sender_email_address = 2;     // Email address of the sender
  string subject = 3;                  // Email subject line
  string body = 4;                     // Email body text
  bytes attached_image = 5;            // Raw image bytes (PNG, JPEG, etc.)
  string body_hash = 6;                // SHA-256 hash of the body text
  string image_hash = 7;               // SHA-256 hash of the image bytes
}
```

## Building the Project

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 11 or later
- **Android SDK**: API level 24+ (Android 7.0)
- **Gradle**: 8.0+ (included via wrapper)

### Build Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd FavEmailApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory
   - Wait for Gradle sync to complete

3. **Build the Project**
   - Click `Build > Make Project` or press `Ctrl+F9` (Windows/Linux) / `Cmd+F9` (Mac)
   - Or use the command line:
     ```bash
     ./gradlew build
     ```

4. **Generate Release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Running the App

### On Device/Emulator

1. **Connect a device** or start an emulator (API 24+)
2. **Run the app**:
   - Click the Run button (▶️) in Android Studio
   - Or use command line: `./gradlew installDebug`

3. **Select Email File**:
   - On app startup, a file picker will automatically open
   - Navigate to and select a `.pb` file
   - The app will parse and display the email with shimmer loading animation

### Creating a Sample Email File

To test the app, you'll need a `.pb` file. Here's a simple Python script to generate one:

```python
import struct

# Simple email message structure
sender_name = "John Doe"
sender_email = "john.doe@example.com"
subject = "Test Email"
body = "This is a test email body."
attached_image = b""  # Empty image for simplicity
body_hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"  # SHA-256 of empty string
image_hash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

# Create protobuf message (simplified - you'll need proper protobuf encoding)
# For proper generation, use protoc to generate Python code:
# protoc --python_out=. app/src/main/proto/email_message.proto
```

**Note**: For proper Protocol Buffer encoding, use the `protoc` compiler to generate language-specific code, or use the generated Java classes from the Android project.

## External Packages Used

### Core Dependencies
- **Hilt** (2.51.1): Dependency injection framework
- **Room** (2.6.1): Database abstraction layer
- **SQLCipher** (4.5.4): Database encryption
- **Protocol Buffers** (3.25.3): Data serialization
- **Jetpack Compose BOM** (2024.09.00): Modern UI toolkit
- **Kotlin Coroutines** (1.7.3): Asynchronous programming
- **Lifecycle** (2.9.4): ViewModel and lifecycle-aware components
- **AndroidX Security Crypto** (1.1.0-alpha06): EncryptedSharedPreferences

### UI Components
- **Material 3**: Material Design 3 components
- **Navigation Compose** (2.8.4): Navigation between screens
- **Activity Compose** (1.11.0): Compose integration with Activities

## Architecture Details

### MVVM Pattern

- **Model**: `EmailMessage` data class, Protocol Buffer schema, and Room entities
- **View**: `EmailScreen` composable with UI components
- **ViewModel**: `EmailViewModel` manages UI state and business logic
- **Repository**: `EmailRepository` handles data operations and hash verification

### Data Flow

1. User selects `.pb` file via system file picker
2. `MainActivity` reads file and passes to `EmailViewModel`
3. `EmailViewModel` uses `ProtobufParser` to parse the file (returns `Result<EmailMessage>`)
4. Parsed data is saved to encrypted database via `EmailRepository`
5. `EmailRepository` verifies SHA-256 hashes during save
6. Database emits updates via Flow
7. UI observes Flow and updates automatically with animations

### State Management

- **UI State**: `StateFlow<EmailUiState>` for UI state (Initial, Loading, Success, Error)
- **Data State**: `Flow<EmailMessage?>` from Room for reactive database updates
- **App State**: `EmailAppState` for centralized state hoisting (theme, language, file selection)
- **State Preservation**: State survives configuration changes (screen rotation, language change, etc.)

### Error Handling

The app uses a robust error handling pattern:

- **Result Pattern**: `Result<T>` sealed class for type-safe success/error handling
- **Error Hierarchy**: `AppError` sealed class with typed error subtypes:
  - `FileError`: File-related errors (empty, corrupted, not found, etc.)
  - `DatabaseError`: Database operation errors
  - `NetworkError`: Network-related errors (for future use)
  - `Unknown`: Generic error with original message
- **Error Mapper**: `ErrorMapper` centralizes exception-to-error conversion
- **User-Friendly Messages**: All errors display localized, user-friendly messages

### Dependency Injection

Hilt modules provide:
- **DatabaseModule**: Encrypted database and DAO instances
- **RepositoryModule**: Email repository instance
- **Application**: `FavEmailApplication` annotated with `@HiltAndroidApp`
- **ViewModel**: `EmailViewModel` annotated with `@HiltViewModel`

## UI/UX Features

### Shimmer Loading Effect

The app features a beautiful shimmer loading animation that:
- Mimics the actual email content layout
- Shows placeholder cards for sender, subject, body, image, and verification status
- Uses smooth gradient animations with theme-aware colors
- Provides visual feedback during file parsing

### Animations

- **State Transitions**: Smooth fade and slide animations between UI states
- **Card Animations**: Staggered card appearance with fade-in and slide-up effects
- **Shimmer Animation**: Continuous gradient sweep animation (1800ms duration)

### Theme Support

- **Light Mode**: Bright, clean interface
- **Dark Mode**: Dark theme with proper contrast
- **System Mode**: Automatically follows device theme
- **Theme Switcher**: Easy theme selection via dropdown in app bar

### Localization

- **English**: Default language
- **Kinyarwanda (Ikinyarwanda)**: Full translation support
- **Language Switcher**: Easy language selection via dropdown in app bar
- **Persistent**: Language preference saved and restored across app restarts
- **Dynamic**: UI updates immediately on language change

## Hash Verification

The app verifies data integrity by:

1. Computing SHA-256 hash of email body text
2. Computing SHA-256 hash of attached image bytes
3. Comparing computed hashes with stored hashes from the protobuf file
4. Displaying verification badges:
   - ✅ Green checkmark with "Verified" text if hash matches
   - ❌ Red X with "Failed" text if hash doesn't match
5. Overall verification status card shows:
   - "Email Verified" if both hashes match
   - "Verification Failed" if either hash fails

## Database Encryption

### Security Features

- **SQLCipher Encryption**: All database data encrypted at rest
- **Secure Key Storage**: Database password stored securely using:
  - **Primary Method**: `EncryptedSharedPreferences` with Android Keystore master key
  - **Fallback 1**: Android Keystore (hardware-backed security when available)
  - **Fallback 2**: Device-specific deterministic password (ensures compatibility)
- **No Plain Text**: Password is never stored in plain text in code or SharedPreferences
- **Unique Per Device**: Each device gets a unique password
- **Automatic Recovery**: Handles password mismatch by recreating database with new secure password

### Key Management

The `SecureKeyManager` utility class:
- Generates secure random passwords
- Stores passwords using Android's secure storage mechanisms
- Provides fallback methods for device compatibility
- Handles key rotation and database migration

## Permissions

- **Android 13+ (API 33+)**: No permissions required (uses scoped storage)
- **Android 12 and below**: Requires `READ_EXTERNAL_STORAGE` permission
- **Automatic Request**: Permission is requested automatically when needed
- **Graceful Handling**: Shows appropriate error messages if permission is denied

## Error Handling

The app handles various error scenarios with user-friendly messages:

### File Errors
- File not found
- File cannot be read
- File is empty or corrupted
- Invalid Protocol Buffer format
- Missing required email data

### Database Errors
- Save operation failures
- Load operation failures
- Database corruption (auto-recovery)

### User Feedback
- Clear error messages in user's selected language
- Retry options for recoverable errors
- Visual error indicators (icons and colors)

## Testing

### Manual Testing Checklist

1. **Valid Email File**:
   - Create a sample `.pb` file with valid data
   - Load it in the app
   - Verify email displays correctly
   - Verify hash verification badges show correctly
   - Verify all cards animate in smoothly

2. **Invalid File**:
   - Try loading a non-protobuf file
   - Try loading an empty file
   - Try loading a corrupted protobuf file
   - Verify appropriate error messages are shown

3. **Configuration Changes**:
   - Load an email
   - Rotate the device
   - Change language
   - Change theme
   - Verify email data persists and UI updates correctly

4. **Permissions**:
   - On Android < 13, deny storage permission
   - Verify appropriate error message
   - Grant permission and verify file picker works

5. **Theme Switching**:
   - Switch between Light, Dark, and System themes
   - Verify colors update correctly
   - Verify shimmer effect adapts to theme

6. **Language Switching**:
   - Switch between English and Kinyarwanda
   - Verify all text updates
   - Verify file picker doesn't reopen unnecessarily

7. **Multiple Files**:
   - Load one email file
   - Use "Load Another Email" button
   - Load a different email file
   - Verify new email displays correctly

## Known Limitations

- Sample email generator requires manual setup or protoc compiler
- File picker opens automatically on first launch (by design)
- Image display limited to common formats (PNG, JPEG)
- No support for multiple attachments (single image only)

## Future Improvements

- Add support for multiple email files with list view
- Implement email search functionality
- Add unit tests and UI tests
- Support for more image formats
- Add email export functionality
- Implement email sharing
- Add support for multiple attachments
- Improve error messages with more technical details in debug mode

## Code Quality

### Design Patterns Used

- **MVVM**: Model-View-ViewModel architecture
- **Repository Pattern**: Data abstraction layer
- **Dependency Injection**: Hilt for loose coupling
- **Result Pattern**: Type-safe error handling
- **State Hoisting**: Centralized state management
- **Error Mapper Pattern**: Centralized exception-to-error conversion

### Best Practices

- Clean code with descriptive names
- Separation of concerns
- Reactive programming with Flow
- Proper error handling
- Secure data storage
- Localized user-facing strings
- Material Design 3 guidelines
- Accessibility considerations

## License

This project is created for the QT Global Software Ltd Android Developer practical test.

## Contact

For questions or issues, please contact the development team.

---

**Built with ❤️ using Kotlin, Jetpack Compose, and modern Android development practices.**
