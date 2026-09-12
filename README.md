# StayHard

StayHard is an Android application designed to keep you motivated. It fetches powerful motivational quotes and automatically sets them as your lock screen wallpaper. Stay disciplined, stay hard!

## Features

- **Daily Motivation:** Fetches random quotes from a remote API.
- **Lock Screen Wallpaper:** Automatically generates a beautiful wallpaper with the quote and sets it on your lock screen.
- **Background Updates:** Uses Android's AlarmManager to refresh the wallpaper in the background periodically.
- **Minimalist UI:** Simple and clean user interface to manually refresh your daily quote.

## Architecture

The project is built with a focus on simplicity and efficiency:
- **Language:** Kotlin
- **Architecture Pattern:** MVVM (Model-View-ViewModel)
- **UI:** XML Layouts (ConstraintLayout)
- **Networking:** Retrofit + OkHttp
- **Concurrency:** Kotlin Coroutines & LiveData
- **Dependency Management:** Custom lightweight Service Locator (`Dependencies.kt`)

## Getting Started

### Prerequisites
- Android Studio Ladybug or later
- Android SDK 35
- JDK 11+

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/RADJOU-Dinesh-24003262/StayHard.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device running Android 7.0 (API 24) or higher.

## Permissions

The app requires the `SET_WALLPAPER` permission to function correctly. This is requested at runtime on modern Android versions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.