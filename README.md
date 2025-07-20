# MyRide

## 🚗 MyRide

MyRide is a ride-booking Android application built using modern Android development tools and best practices — Jetpack Compose, Hilt, MVVM architecture, Room, and Google Places API.

---

## 📦 Features

- 🔍 Autocomplete for pickup and destination using Google Places
- 💵 Fare estimation with surge and traffic multipliers
- 🚗 Driver assignment simulation
- 🕓 Estimated time of arrival display
- 📖 Local ride history using Room
- ✨ Polished, responsive UI with Jetpack Compose

---

## 🛠️ Project Setup Instructions

### ✅ Prerequisites

- Android Studio Hedgehog or newer
- Kotlin 1.9+
- Android SDK 24+
- Gradle 8.0+
- A valid [Google Places API](https://developers.google.com/maps/documentation/places/web-service/get-api-key) key

---

### 🔑 Add Your API Key Securely

1. In your project root, open or create the `local.properties` file.
2. Add the following line (replace with your actual key): MAPS_API_KEY=your_google_places_api_key_here


3. Then in `app/build.gradle.kts`, make sure the following is inside `android`:

```kotlin
buildFeatures {
    buildConfig = true
}

defaultConfig {
    ...
    buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
}

```

4. Access the API key in Kotlin: BuildConfig.MAPS_API_KEY

## ▶️ How to Run the App
### From Android Studio
1. Open the project in Android Studio.
2. Let Gradle sync finish.
3. Connect a device or start an emulator.
4. Press Run ▶️ or use Shift + F10.

---

## 🧪 How to Execute Tests

### ✅ Unit Tests
1. Run unit tests (e.g. FareCalculatorTest.kt, RideRepositoryTest.kt):

2. In Android Studio:
   Right-click on the test folder or any test class.

3. Click Run Tests.

---

### 📱 Instrumented Tests
1. Run UI or Room database tests located in androidTest/:

2. In Android Studio:
   - Right-click the androidTest folder > Run Tests.

From Terminal (device/emulator required):
   ./gradlew connectedAndroidTest

---

## 🗂 Project Structure (MVVM Pattern)

```
com.newagedavid.myride/
│
├── data/               # Local data layer (Room DB, DAO, entities) \n
├── di/                 # Hilt dependency injection modules
├── domain/             # Business logic & data models
├── repository/         # Repository abstraction + impl
├── ui/                 # Composable UI and screens
├── viewmodel/          # ViewModels with business logic
├── MainActivity.kt     # App entry point with Hilt & Compose
└── AppNavigation.kt    # Jetpack Compose navigation flow
```

---

## 🧰 Tech Stack

- **Kotlin**
- **Jetpack Compose** (UI)
- **Room** (local persistence)
- **Hilt** (Dependency Injection)
- **MVVM** architecture
- **Google Places API**
- **JUnit** + **MockK** (unit tests)
- **Espresso** (instrumented UI tests)

---

## 📃 License
This project is licensed under the MIT License.

---

## ✨ Author
### David Solomon
**GitHub:** @SDavid24

**Twitter:** @newagadavid



