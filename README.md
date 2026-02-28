# Habit Tracker - Production-Grade Android Application

[![Build Status](https://github.com/clusterloggs/HabitTracker/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/clusterloggs/HabitTracker/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Compose-2024.12.01-green.svg)](https://developer.android.com/jetpack/compose)

A fully-featured native Android habit tracking application built with modern Android development practices, clean architecture, and a production-ready tech stack.

## Overview

Habit Tracker is a comprehensive habit tracking application that helps users build and maintain healthy habits. The app combines habit management with analytics, device usage tracking, and intelligent reminders to provide users with actionable insights into their daily routines.

### Key Features

- **Habit Management**: Create, update, and organize habits with customizable icons, colors, and categories
- **Smart Reminders**: Exact alarms with Material 3 notifications that persist across device reboots
- **Task Logging**: Record completions with duration, mood ratings, and optional notes
- **Analytics Dashboard**: View streaks, completion rates, and historical performance with Vico charts
- **Device Usage Tracking**: Correlate habit completion with app usage patterns via UsageStatsManager
- **Offline-First Architecture**: Full functionality without internet; ready for future cloud sync

## Architecture

The project follows **Clean Architecture** with strict separation of concerns:

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Compose UI, ViewModels)           │
├─────────────────────────────────────┤
│     Domain Layer                    │
│  (Use Cases, Models, Interfaces)    │
├─────────────────────────────────────┤
│     Data Layer                      │
│  (Repository Implementations)       │
├─────────────────────────────────────┤
│     Framework & Drivers             │
│  (Room, DataStore, APIs)            │
└─────────────────────────────────────┘
```

### Project Structure

```
├── :app                    # Main application module
├── :core
│   ├── :database          # Room database setup, entities, DAOs
│   ├── :datastore         # Encrypted user preferences
│   ├── :network           # Retrofit setup (reserved for sync)
│   └── :ui                # Material 3 theme, shared composables
├── :feature
│   ├── :habits            # Habit CRUD, HomeScreen
│   ├── :reminders         # Alarms, notifications, WorkManager
│   ├── :analytics         # Stats, charts, Vico integration
│   └── :usagestats        # Device usage collection
└── :config                # Detekt, linting
```

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Kotlin | 2.1.0 |
| **UI Framework** | Jetpack Compose | 2024.12.01 |
| **Material Design** | Material 3 | 1.3.1 |
| **Architecture** | Clean Architecture + MVVM | - |
| **Dependency Injection** | Hilt | 2.52 |
| **Database** | Room + SQLCipher | 2.7.0a02 / 4.5.4 |
| **Preferences** | DataStore (Proto) | 1.2.1 |
| **Background** | WorkManager | 2.10.0 |
| **Async** | Coroutines + Flow | 1.10.1 |
| **Charts** | Vico | 1.15.1 |
| **Navigation** | Navigation Compose | 2.8.5 |
| **Testing** | JUnit5, MockK, Turbine, Compose UI Test | Latest |
| **Code Quality** | Detekt, Ktlint | 1.23.7 / 1.3.1 |
| **CI/CD** | GitHub Actions | - |

## Getting Started

### Prerequisites

- **Android Studio**: Latest Canary or Stable release
- **Java Development Kit**: JDK 17 or newer
- **Android SDK**: API level 35 (compileSdk: 35)
- **Gradle**: 8.9.0 (managed by gradle wrapper)

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd habit-tracker.apk
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run debug variant**
   ```bash
   ./gradlew installDebug
   ```

4. **Run tests**
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew connectedAndroidTest
   ```

5. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```

## Security & Privacy

- **Database Encryption**: Room database encrypted with SQLCipher
- **Secure Preferences**: DataStore with secure encryption
- **No Sensitive Logging**: Debug logs disabled in release builds
- **Permission Handling**: Graceful degradation when permissions denied
- **Offline-First**: All user data stored locally, never transmitted without consent

## Permissions

The app requests the following permissions (with rationale dialogs):

```xml
POST_NOTIFICATIONS          <!-- System notifications for reminders -->
SCHEDULE_EXACT_ALARM        <!-- Precise timing for habit reminders -->
PACKAGE_USAGE_STATS         <!-- Device screen time analysis -->
RECEIVE_BOOT_COMPLETED      <!-- Persist alarms across reboots -->
```

## Testing Strategy

### Unit Tests
- Use cases and ViewModels tested with MockK and Turbine
- Repository patterns with fake implementations
- **Target Coverage**: 80%+

### Integration Tests
- Room DAOs with in-memory database
- Database migrations

### UI Tests
- Critical user flows with Compose UI Test
- Hilt testing for dependency injection

### Running Tests
```bash
# Unit tests only
./gradlew testDebugUnitTest

# UI/Integration tests
./gradlew connectedAndroidTest

# All tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

## Performance Optimizations

- **Lazy Loading**: Paging 3 for habit history
- **Baseline Profiles**: Compose startup optimization
- **R8 full mode**: Enabled in release builds
- **Database Indexing**: Strategic indices on frequently queried columns
- **Flow-based**: Reactive updates, no unnecessary recomposition

## CI/CD Pipeline

GitHub Actions workflow (`ci-cd.yml`) includes:

1. **Build**: Gradle build with caching
2. **Unit Tests**: `testDebugUnitTest` with JUnit5
3. **Static Analysis**: Detekt, Ktlint linting
4. **Code Coverage**: Codecov integration, min 80% target
5. **Release Build**: Signed APK generation (main branch only)
6. **Release Publishing**: Automated GitHub releases with APK artifacts

### Setting Up CI/CD

1. **GitHub Secrets** (required for signing):
   - `KEYSTORE_FILE_BASE64`: Base64-encoded keystore file
   - `KEYSTORE_PASSWORD`: Keystore password
   - `KEY_ALIAS`: Key alias in keystore
   - `KEY_PASSWORD`: Key password
   - `CODECOV_TOKEN`: Codecov.io token

2. **Keystore Generation**:
   ```bash
   keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release-key
   base64 -w 0 release.jks | xclip -selection clipboard
   ```

## Future Enhancements

### Phase 2: Cloud Sync
- Backend API integration (Retrofit ready)
- Firebase Firestore support
- Real-time sync across devices
- User authentication

### Phase 3: Advanced Analytics
- Machine learning habit predictions
- Habit success correlations
- Personalized recommendations
- Export to CSV/PDF

### Phase 4: Social Features
- Share habit progress with friends
- Accountability partnerships
- Community challenges
- Social media integration

## Documentation

- **Architecture Decisions**: See `/docs/architecture.md`
- **Database Schema**: See Room entities in `:core:database`
- **API Contracts**: Domain models in `domain/model/`
- **Compose Patterns**: `core:ui` composables with KDoc comments

## Contributing

This project follows Android best practices and clean architecture principles:

1. **Code Style**: Kotlin Official Style Guide (enforced by Ktlint)
2. **Static Analysis**: Detekt rules in `config/detekt/`
3. **Commit Standards**: Descriptive commit messages, conventional commits
4. **PR Requirements**:
   - Pass all CI checks
   - Minimum 80% code coverage
   - Architecture design review
   - No security vulnerabilities



## Support

For issues, feature requests, or questions:
1. Check existing GitHub issues
2. Create a new issue with detailed description
3. Include logs and device information for bugs

---

**Last Updated**: February 2025
**Status**: Production Ready (MVP)
