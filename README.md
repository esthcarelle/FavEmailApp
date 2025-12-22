# FavEmailApp - Email Viewer with Protocol Buffers

An Android application that loads and displays email messages stored in Protocol Buffer format, with hash verification for data integrity.

## Features

- ✅ **Protocol Buffer Support**: Loads and parses `.pb` files containing email messages
- ✅ **Hash Verification**: Verifies SHA-256 hashes for email body and attached images
- ✅ **Encrypted Database**: Stores email data in an encrypted SQLCipher database
- ✅ **MVVM Architecture**: Clean separation of concerns with ViewModel and Repository
- ✅ **Dependency Injection**: Uses Hilt for dependency injection
- ✅ **Jetpack Compose**: Modern UI built with Compose
- ✅ **Coroutines & Flow**: Reactive data handling with Kotlin Coroutines and Flow
- ✅ **State Preservation**: Data persists across configuration changes
- ✅ **Permission Handling**: Proper handling of storage permissions for different Android versions

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room + SQLCipher (encrypted)
- **Data Format**: Protocol Buffers
- **Async Operations**: Kotlin Coroutines & Flow
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36

## Project Structure

```
app/src/main/
├── java/com/qtsoftwareltd/favemailapp/
│   ├── data/
│   │   ├── di/              # Hilt dependency injection modules
│   │   ├── local/            # Room database and DAO
│   │   ├── model/            # Data models
│   │   ├── parser/           # Protocol Buffer parser
│   │   └── repository/       # Repository layer
│   ├── ui/
│   │   ├── viewmodel/        # ViewModels
│   │   └── EmailScreen.kt    # Main UI screen
│   ├── util/                 # Utility classes (HashUtil)
│   ├── FavEmailApplication.kt # Application class
│   └── MainActivity.kt       # Main activity
├── proto/
│   └── email_message.proto    # Protocol Buffer schema
└── res/                      # Resources
```

## Protocol Buffer Schema

The email message is defined in `email_message.proto`:

```protobuf
message EmailMessage {
  string sender_name = 1;
  string sender_email_address = 2;
  string subject = 3;
  string body = 4;
  bytes attached_image = 5;
  string body_hash = 6;      // SHA-256 hash of body
  string image_hash = 7;     // SHA-256 hash of image
}
```

## Building the Project

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK with API level 24+

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

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

4. **Build the Project**
   - Click `Build > Make Project` or press `Ctrl+F9` (Windows/Linux) / `Cmd+F9` (Mac)
   - Or use the command line:
     ```bash
     ./gradlew build
     ```

5. **Generate Release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Running the App

### On Device/Emulator

1. **Connect a device** or start an emulator (API 24+)
2. **Run the app**:
   - Click the Run button in Android Studio
   - Or use command line: `./gradlew installDebug`

3. **Select Email File**:
   - On app startup, a file picker will automatically open
   - Navigate to and select a `.pb` file
   - The app will parse and display the email

### Creating a Sample Email File

A sample email file generator script is provided. To create a test file:

**Option 1: Using Python (requires protobuf Python library)**
```bash
# First, generate Python protobuf code
protoc --python_out=. app/src/main/proto/email_message.proto

# Then run the generator
python3 generate_sample_email.py
```

**Option 2: Using Kotlin (requires compiled protobuf classes)**
```bash
# After building the project, the protobuf classes will be generated
# You can create a simple test file using the Kotlin script
```

## External Packages Used

- **Hilt** (2.51.1): Dependency injection framework
- **Room** (2.6.1): Database abstraction layer
- **SQLCipher** (4.5.4): Database encryption
- **Protocol Buffers** (3.25.3): Data serialization
- **Jetpack Compose**: Modern UI toolkit
- **Coroutines** (1.7.3): Asynchronous programming
- **Lifecycle**: ViewModel and lifecycle-aware components

## Architecture Details

### MVVM Pattern

- **Model**: `EmailMessage` data class and Protocol Buffer schema
- **View**: `EmailScreen` composable with UI components
- **ViewModel**: `EmailViewModel` manages UI state and business logic

### Data Flow

1. User selects `.pb` file via file picker
2. `MainActivity` reads file and passes to `EmailViewModel`
3. `EmailViewModel` uses `ProtobufParser` to parse the file
4. Parsed data is saved to encrypted database via `EmailRepository`
5. `EmailRepository` verifies hashes during save
6. Database emits updates via Flow
7. UI observes Flow and updates automatically

### State Management

- Uses `StateFlow` for UI state (Initial, Loading, Success, Error)
- Uses `Flow` from Room for reactive database updates
- State survives configuration changes (screen rotation, etc.)

### Dependency Injection

Hilt modules provide:
- `DatabaseModule`: Encrypted database and DAO
- `RepositoryModule`: Email repository

## Hash Verification

The app verifies data integrity by:

1. Computing SHA-256 hash of email body text
2. Computing SHA-256 hash of attached image bytes
3. Comparing computed hashes with stored hashes from the protobuf file
4. Displaying verification badges:
   - ✅ Green checkmark if hash matches
   - ❌ Red X if hash doesn't match

## Database Encryption

- Uses SQLCipher to encrypt the Room database
- All email data stored in the database is encrypted at rest
- **Database password is securely stored using:**
  - **Primary method**: EncryptedSharedPreferences (uses Android Keystore master key)
  - **Fallback 1**: Android Keystore (hardware-backed security when available)
  - **Fallback 2**: Device-specific deterministic password (ensures app works on all devices)
- Password is **never stored in plain text** in the code
- The password is generated on first use and stored securely
- Each device gets a unique password, ensuring database security

## Permissions

- **Android 13+ (API 33+)**: No permissions required (uses scoped storage)
- **Android 12 and below**: Requires `READ_EXTERNAL_STORAGE` permission
- Permission is requested automatically when needed

## Error Handling

The app handles various error scenarios:

- File not found or cannot be opened
- Invalid Protocol Buffer format
- Missing required fields
- Hash verification failures
- Database errors

All errors are displayed to the user with clear messages and retry options.

## Testing

### Manual Testing

1. **Test with valid email file**:
   - Create a sample `.pb` file
   - Load it in the app
   - Verify email displays correctly
   - Verify hash verification badges show correctly

2. **Test with invalid file**:
   - Try loading a non-protobuf file
   - Verify error message is shown

3. **Test configuration changes**:
   - Load an email
   - Rotate the device
   - Verify email data persists

4. **Test permissions**:
   - On Android < 13, deny storage permission
   - Verify appropriate error message

## Known Limitations

- Sample email generator requires manual setup
- File picker opens automatically on startup (could be made optional)

## Future Improvements

- Add support for multiple email files
- Implement email list view
- Add search functionality
- Improve error messages with more details
- Add unit tests and UI tests

## License

This project is created for the QT Global Software Ltd Android Developer practical test.

## Contact

For questions or issues, please contact the development team.

