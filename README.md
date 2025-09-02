# Sticker Creator

An Android app that allows users to select photos from their gallery or camera and crop them into 512x512 WebP stickers.

## Features

- 📸 **Photo Selection**: Choose photos from gallery or take new ones with camera
- ✂️ **Interactive Cropping**: Drag to select and resize crop areas with visual handles
- 🖼️ **WebP Export**: Automatically converts and saves cropped images as 512x512 WebP files
- 🔒 **Permission Handling**: Proper runtime permission requests for camera and storage
- 🎨 **Modern UI**: Built with Jetpack Compose and Material Design 3
- ✅ **Comprehensive Testing**: Unit tests and UI tests included

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Permissions**: Accompanist Permissions
- **Testing**: JUnit, Mockito, Compose Testing

## Requirements

- Android 7.0 (API level 24) or higher
- Camera permission for taking photos
- Storage permission for accessing gallery

## Project Structure

```
app/
├── src/main/java/com/stickercreator/app/
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── home/          # Photo selection screen
│   │   │   └── crop/          # Crop and edit screen
│   │   ├── components/        # Reusable UI components
│   │   ├── navigation/        # Navigation setup
│   │   └── theme/            # App theming
│   ├── data/
│   │   └── repository/       # Image processing and saving
│   └── di/                   # Dependency injection modules
├── src/test/                 # Unit tests
└── src/androidTest/          # UI tests
```

## Building the App

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

## Usage

1. **Select Photo**: Choose between gallery or camera options
2. **Crop Image**: Drag the corners to resize the crop area or drag inside to move it
3. **Save Sticker**: The app automatically resizes to 512x512 and saves as WebP format
4. **Find Your Stickers**: Saved in Pictures/StickerCreator folder

## Testing

Run tests using:
```bash
./gradlew test           # Unit tests
./gradlew connectedAndroidTest  # UI tests
```

## Permissions

The app requests these permissions:
- `CAMERA` - To take photos
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` - To access gallery photos
- `WRITE_EXTERNAL_STORAGE` - To save stickers (Android 9 and below)

## License

This project is open source and available under the [MIT License](LICENSE).


## Start emulator

```
   $env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
   & "$env:ANDROID_HOME\emulator\emulator.exe" -avd Medium_Phone_API_36.0
```
Install App
```
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH += ";$env:ANDROID_HOME\platform-tools"
   adb install app/build/outputs/apk/debug/app-debug.apk
```

