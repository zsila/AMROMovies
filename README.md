# AMRO Movies

AMRO Movies is an Android application that displays the **Top 100 trending movies of the week** using data from The Movie Database (TMDB).

The app allows users to:

- Browse trending movies
- Filter movies by genre (within the Top 100 list)
- Sort movies by popularity, title, or release date
- View detailed movie information

---

## ✨ Features

### 🎬 Movie List
- Displays Top 100 trending movies of the week
- Shows:
  - Poster image
  - Movie title
  - Genres
  - Release date
- Supports:
  - Genre filtering (within the same Top 100 set)
  - Sorting:
    - Popularity
    - Title
    - Release date
  - Ascending / Descending order
- Loading & error states

---

### 🎥 Movie Detail
Shows detailed information for a selected movie:

- Title
- Poster
- Genres
- Overview
- Release date
- Runtime
- Status
- Vote average & vote count
- Budget & revenue
- IMDb link (if available)

---

## 🏗 Architecture

This project follows a layered architecture inspired by **Clean Architecture** principles:

presentation → domain → data

### **Data Layer**
Contains:
- Retrofit API interface
- DTO models
- Repository implementation
- Mappers (DTO → Domain)

Responsibilities:
- Networking
- Data transformation

---

### **Domain Layer**
Contains:
- Business models
- Repository interface
- Use cases

Responsibilities:
- Business logic (filtering, sorting)
- No Android / Retrofit dependencies

---

### **Presentation Layer**
Contains:
- ViewModels
- Compose UI screens
- UI state management

Responsibilities:
- UI logic
- State handling

---

## 🛠 Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **Dependency Injection:** Hilt
- **Networking:** Retrofit + OkHttp
- **JSON Parsing:** Gson
- **Image Loading:** Coil
- **Concurrency:** Kotlin Coroutines
- **State Management:** StateFlow
- **Testing:** JUnit4, Coroutines Test, MockWebServer

---

## 🚀 Getting Started

### 1️⃣ Obtain TMDB API Key 
Create an API key from:

https://www.themoviedb.org/

---

### 2️⃣ Configure `local.properties`

Create (or edit) `local.properties` in the project root:

```properties
TMDB_API_KEY=YOUR_API_KEY (paste just the key, wihout a " at the start/end of the string)
```

---
### 3️⃣ Build & Run

1. Open project in Android Studio

2. Sync Gradle

3. Run the app configuration

---

### 🧪 Running Tests

Run unit tests:

    ./gradlew test 

Run UI tests:
  
    ./gradlew connectedAndroidTest

---

### ⚠️ Notes & Tradeoffs

- API key is injected via BuildConfig using local.properties

- Error handling is intentionally simple for assignment scope

- Paging is handled manually to fetch Top 100 movies

---

### 🔮 Possible Improvements

 - Offline caching (Room)

 - Paging 3 integration

 - Enhanced error handling (typed failures)

 - UI animations and nicer-looking UI

 - More UI & integration tests




