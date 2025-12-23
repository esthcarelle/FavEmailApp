# FavEmailApp

**GitHub Repository**: [https://github.com/esthcarelle/FavEmailApp](https://github.com/esthcarelle/FavEmailApp)

Android app that loads and displays email messages from Protocol Buffer files. Includes hash verification, encrypted storage, and a modern UI built with Jetpack Compose.

## What It Does

- Loads `.pb` files containing email messages
- Verifies SHA-256 hashes for email body and images
- Stores emails in an encrypted SQLCipher database
- Displays email content with verification badges
- Supports light/dark themes and English/Kinyarwanda languages

## Tech Stack

- **Kotlin** 2.0.21
- **Jetpack Compose** (Material 3)
- **MVVM** architecture
- **Hilt** for dependency injection
- **Room + SQLCipher** for encrypted database
- **Protocol Buffers** 3.25.3
- **Coroutines & Flow** for async operations

**Min SDK**: 24 (Android 7.0) | **Target SDK**: 36

## Building

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build: `./gradlew build`
5. Generate APK: `./gradlew assembleRelease`

APK location: `app/build/outputs/apk/release/app-release.apk`

## Running

1. Connect device or start emulator (API 24+)
2. Run from Android Studio or: `./gradlew installDebug`
3. App opens file picker automatically
4. Select a `.pb` file to view

## Sample File

A sample `sample_email.pb` file is included in the project root. You can also generate one by running:

```bash
./generate_sample_pb.sh
```

Or use the test utility in `app/src/test/java/com/qtsoftwareltd/favemailapp/util/SampleEmailGenerator.kt`.

## Protocol Buffer Schema

The email message structure is defined in `app/src/main/proto/email_message.proto`:

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

## Architecture

**MVVM Pattern**:
- Model: `EmailMessage` data class and Room entities
- View: `EmailScreen` composable
- ViewModel: `EmailViewModel` manages state and business logic
- Repository: `EmailRepository` handles data operations and hash verification

**Data Flow**:
1. User selects `.pb` file
2. `ProtobufParser` parses the file
3. `EmailRepository` verifies hashes and saves to encrypted database
4. UI observes database Flow and updates automatically

## Key Features

### Security
- Database encrypted with SQLCipher
- Password stored using Android Keystore and EncryptedSharedPreferences
- No plain text passwords in code

### UI/UX
- Shimmer loading animation
- Smooth card animations
- Theme support (Light/Dark/System)
- Localization (English/Kinyarwanda)
- Material Design 3

### Error Handling
- Result pattern for type-safe error handling
- Sealed class error hierarchy
- User-friendly error messages in selected language

## Dependencies

- Hilt 2.51.1
- Room 2.6.1
- SQLCipher 4.5.4
- Protocol Buffers 3.25.3
- Jetpack Compose BOM 2024.09.00
- Coroutines 1.7.3
- AndroidX Security Crypto 1.1.0-alpha06

## Testing

Run unit tests:
```bash
./gradlew test
```

The test suite includes:
- Hash computation tests
- Protocol Buffer parsing tests
- Repository hash verification tests
- Sample file generation test

## Project Structure

```
app/src/main/
├── java/com/qtsoftwareltd/favemailapp/
│   ├── data/          # Data layer (repository, parser, database)
│   ├── ui/            # UI layer (Compose screens, ViewModels)
│   ├── util/           # Utilities (hash, locale, key management)
│   └── MainActivity.kt
├── proto/
│   └── email_message.proto
└── res/
    ├── values/         # English strings
    └── values-rw/      # Kinyarwanda strings
```

## Permissions

- **Android 13+**: No permissions needed (scoped storage)
- **Android 12 and below**: Requires `READ_EXTERNAL_STORAGE`

## Known Issues

- File picker opens automatically on first launch
- Single image attachment only
- PNG/JPEG image formats supported

## License

Created for QT Global Software Ltd Android Developer practical test.
