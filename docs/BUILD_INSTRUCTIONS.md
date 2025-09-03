# Build Instructions for Sticker Creator

## Quick Start

### Prerequisites
- Java 11 or higher
- Android SDK (will be auto-downloaded by Gradle)

### Building the App

1. **Debug Build (for development)**:
   ```bash
   .\gradlew.bat assembleDebug
   ```
   APK location: `app/build/outputs/apk/debug/app-debug.apk`

2. **Release Build (for production)**:
   ```bash
   .\gradlew.bat assembleRelease
   ```
   APK location: `app/build/outputs/apk/release/app-release-unsigned.apk`

3. **Run Tests**:
   ```bash
   .\gradlew.bat test
   ```

4. **Run UI Tests** (requires connected device/emulator):
   ```bash
   .\gradlew.bat connectedAndroidTest
   ```

### Installation

1. Enable "Unknown Sources" on your Android device
2. Transfer the APK to your device
3. Install the APK

### Development

- Open project in Android Studio
- Sync Gradle files
- Run on device/emulator

## Build Status ✅

- ✅ **Debug Build**: Successfully compiled
- ✅ **Release Build**: Successfully compiled  
- ✅ **Unit Tests**: Pass (3 tests)
- ✅ **Code Quality**: No lint errors
- ⚠️ **UI Tests**: Require device/emulator to run

## Project Features

- 📸 Photo selection from gallery/camera
- ✂️ Interactive crop interface
- 🖼️ 512x512 WebP export
- 🔒 Runtime permission handling
- 🎨 Modern Material Design 3 UI
- 🏗️ Clean MVVM architecture
- ✅ Comprehensive testing setup

