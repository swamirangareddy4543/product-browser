# Product Browser

A cross-platform mobile application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** that lets users browse, search, filter, and inspect products fetched from the [DummyJSON](https://dummyjson.com) public API.

---

## Table of Contents

1. [Business Requirements Summary](#1-business-requirements-summary)
2. [Project Architecture Overview](#2-project-architecture-overview)
3. [Tech Stack](#3-tech-stack)
4. [Build & Run Instructions](#4-build--run-instructions)
   - [Prerequisites](#prerequisites)
   - [Android](#android)
   - [iOS](#ios)
5. [Trade-offs & Assumptions](#5-trade-offs--assumptions)

---

## 1. Business Requirements Summary

| # | Requirement |
|---|-------------|
| 1 | Display a scrollable list of products fetched from a remote API. |
| 2 | Support **infinite scroll** / pagination – load the next page automatically as the user nears the bottom of the list. |
| 3 | Allow users to **search** products by keyword with debounced input (400 ms). |
| 4 | Allow users to **filter** products by **category** via a horizontal chip row. |
| 5 | Tapping a product navigates to a **product detail** screen showing full information: image, price, rating, stock, brand, description, and category. |
| 6 | Provide clear **loading**, **error**, and **empty-state** feedback on every screen. |
| 7 | The app must run on both **Android** (API 24+) and **iOS** (arm64, x64, Simulator arm64). |

---

## 2. Project Architecture Overview

The project follows **Clean Architecture** with a clear separation into three layers, all shared in `commonMain` via Kotlin Multiplatform. Platform-specific code is kept to a minimum.

```
composeApp/src/
├── commonMain/                    # Shared code (all business & UI logic)
│   └── kotlin/com/example/productbrowser/
│       ├── App.kt                 # Root Composable entry point
│       ├── navigation/
│       │   └── AppNavigation.kt  # Type-safe Compose Navigation graph
│       ├── presentation/          # UI layer – screens & ViewModels
│       │   ├── productlist/
│       │   │   ├── ProductListScreen.kt
│       │   │   └── ProductListViewModel.kt
│       │   ├── productdetail/
│       │   │   ├── ProductDetailScreen.kt
│       │   │   └── ProductDetailViewModel.kt
│       │   └── components/        # Reusable Composables (cards, chips, etc.)
│       ├── domain/                # Domain layer
│       │   ├── model/Product.kt
│       │   ├── repository/ProductRepository.kt   # interface
│       │   └── usecase/           # One class per use-case
│       │       ├── GetProductsUseCase.kt
│       │       ├── GetProductDetailsUseCase.kt
│       │       ├── GetProductsByCategoryUseCase.kt
│       │       ├── GetCategoriesUseCase.kt
│       │       └── SearchProductsUseCase.kt
│       ├── data/                  # Data layer
│       │   ├── remote/
│       │   │   ├── ProductApiService.kt     # Ktor HTTP client interface + impl
│       │   │   └── dto/                     # JSON DTOs (serialized with kotlinx.serialization)
│       │   └── repository/
│       │       └── ProductRepositoryImpl.kt # Maps DTOs → domain models
│       └── di/
│           └── AppModule.kt       # Manual DI – wires all dependencies together
├── androidMain/                   # Android entry point (MainActivity, Ktor Android engine)
└── iosMain/                       # iOS entry point (Ktor Darwin engine)
```

### Layer responsibilities

| Layer | Responsibility |
|-------|---------------|
| **Presentation** | Compose UI screens + `ViewModel` (AndroidX Lifecycle). State is exposed as `StateFlow`. |
| **Domain** | Pure Kotlin use-cases and the `ProductRepository` interface. Zero framework dependencies. |
| **Data** | Ktor HTTP calls, DTO deserialization via `kotlinx.serialization`, and mapping to domain models. |
| **DI (`AppModule`)** | Manual dependency injection – no DI framework required for this scope. |

### Navigation

Compose Navigation with **type-safe routes** (`@Serializable` objects/data classes):

```
ProductListRoute  →  ProductDetailRoute(productId: Int)
```

---

## 3. Tech Stack

| Concern | Library / Tool | Version |
|---------|---------------|---------|
| Multiplatform framework | Kotlin Multiplatform | 2.1.21 |
| UI | Compose Multiplatform | 1.7.3 |
| HTTP client | Ktor (Android + Darwin engines) | 3.0.3 |
| JSON serialization | kotlinx.serialization | 1.7.3 |
| Async | kotlinx.coroutines | 1.9.0 |
| Navigation | Compose Navigation (multiplatform) | 2.8.0-alpha10 |
| Image loading | Coil 3 + Ktor network fetcher | 3.0.4 |
| ViewModel / Lifecycle | AndroidX Lifecycle (multiplatform) | 2.8.4 |
| Build system | Gradle (Kotlin DSL) | 8.x |
| Android compile SDK | 35 | — |
| Android min SDK | 24 | — |

---

## 4. Build & Run Instructions

### Prerequisites

| Tool | Minimum version | Notes |
|------|----------------|-------|
| JDK | 17 | Required by AGP 8.x |
| Android Studio | Hedgehog (2023.1) or newer | Includes the KMP plugin |
| Xcode | 15 | macOS only, required for iOS |
| Kotlin Multiplatform plugin | latest | Install via *Android Studio → Plugins* |
| CocoaPods | optional | Not used; framework is embedded via Xcode |

> **Internet access** is required at build time to resolve Gradle/Maven dependencies. The app also needs network access at runtime to call `https://dummyjson.com`.

---

### Android

#### Run from Android Studio

1. Open the project root (`Product Browser/`) in **Android Studio**.
2. Wait for Gradle sync to complete.
3. Select the **`composeApp`** run configuration and a connected device / emulator (API 24+).
4. Click **Run ▶**.

#### Run from the command line

```bash
# Debug APK – install on a connected device/emulator
./gradlew :composeApp:installDebug

# Or build the APK without installing
./gradlew :composeApp:assembleDebug
# Output: composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

---

### iOS

> **macOS is required** to build for iOS.

#### Option A – Run from Xcode (recommended)

1. Open **`iosApp/iosApp.xcodeproj`** in Xcode.
2. Select a simulator or a provisioned physical device.
3. Set your **Team ID** in `iosApp/Configuration/Config.xcconfig`:
   ```
   TEAM_ID=YOUR_APPLE_TEAM_ID
   ```
   *(Leave blank for the simulator.)*
4. Press **Run ▶** in Xcode. The Xcode build phase calls `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` automatically to compile the shared KMP framework.

#### Option B – Build the framework manually, then open Xcode

```bash
# Build the shared framework for the iOS Simulator (arm64)
./gradlew :composeApp:assembleDebugAppleFramework

# Or for a physical device
./gradlew :composeApp:assembleReleaseAppleFramework
```

Then open `iosApp/iosApp.xcodeproj` and run from Xcode as usual.

---

## 5. Trade-offs & Assumptions

### Assumptions

* **DummyJSON API is the data source.** No authentication or API key is needed; the API is treated as always available. All product data (title, price, images, rating, stock, etc.) reflects what the API returns.
* **No offline support is required.** The app shows an error state with a retry button if the network is unavailable.
* **No persistence / caching layer.** Products are re-fetched each time the app is launched. This keeps the data layer simple.
* **Single user, no authentication.** There is no login, shopping cart, or user account functionality.

### Trade-offs

| Decision | Why | Alternative considered |
|----------|-----|----------------------|
| **Manual DI (`AppModule`)** instead of Koin/Hilt | Keeps dependencies minimal; the object graph is small. | Koin Multiplatform would be more scalable for larger projects. |
| **Pagination only on the product list** (search & category filter return all results) | DummyJSON's `/products/search` and `/products/category/:cat` endpoints do not enforce pagination in the same way; returning all results simplifies UX. | Could add client-side pagination for very large result sets. |
| **ViewModel scoping via `remember`** in the nav graph | Standard KMP-compatible approach before full ViewModel scoping support in Compose Navigation multiplatform. | Hilt `hiltViewModel()` is available on Android only. |
| **Static `AppModule` for DI** | Simple and zero-overhead for a single-screen-flow app. | A proper DI framework (Koin) would provide better testability and lifecycle management. |
| **Coil 3 for image loading** | First-class Compose Multiplatform support with a Ktor network fetcher; shares the same HTTP client. | Kamel is a lighter alternative but has a smaller community. |
| **No unit tests for ViewModels** in this revision | Scope / time constraint. The domain use-cases and repository are designed to be easily testable with `kotlinx-coroutines-test`. | Add ViewModel tests using `TestScope` and fake repository implementations. |

