<div align="center">
  <h1>💈 Koupa — Barber Booking App</h1>
  <p><strong>حجز مواعيد الحلاقة بذكاء</strong></p>
  <p>
    <img src="https://img.shields.io/badge/version-1.0.0-1A7A78?style=for-the-badge" alt="Version">
    <img src="https://img.shields.io/badge/platform-Android-green?style=for-the-badge&logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/language-Kotlin-orange?style=for-the-badge&logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/license-Personal%20Use%20Only-red?style=for-the-badge" alt="License">
  </p>
</div>

---

## 📖 About

**Koupa** is a full-stack Android barber booking application built for the Algerian market. It connects customers with local barbershops through an elegant, RTL-first native experience. The app features a live interactive map, real-time appointment management, and instant push notifications — all powered by a 100% free open-source backend stack.

> 🧠 Designed and developed as an original concept with full intellectual property rights reserved.

---

## ✨ Features

### 👤 Customer
- 📍 **Nearby Barbershops Map** — Interactive OSMDroid map (OpenStreetMap) with circular image markers and real-time distance
- 📅 **Smart Booking System** — Browse available time slots and book in seconds
- 🗺️ **Turn-by-Turn Routing** — OSRM-powered routing polyline from your location to the shop
- 🔔 **Push Notifications** — Instant booking confirmation and 30-minute arrival reminders
- 🔄 **List / Map Toggle** — Smooth tab-based switching between list and map views

### ✂️ Barber (Dashboard)
- 🏪 **Shop Profile Management** — Edit shop info, services, price range, working hours, and photos
- 📆 **Appointment Management** — View, confirm, and cancel appointments in real time
- 📊 **Availability Control** — Define open/closed time slots per day
- 🔔 **Instant New Booking Alerts** — FCM push notification on every new booking

### ⚙️ Tech Highlights
- 🆓 **Zero Google Maps cost** — Migrated to OSMDroid + OSRM (100% free, open-source)
- 🔒 **Row Level Security** — All Supabase tables protected with Postgres RLS policies
- ⚡ **Edge Functions** — Serverless Deno functions for atomic booking and FCM v1 notifications
- ⏰ **Automated Reminders** — pg_cron job sends 30-min pre-appointment FCM reminders

---

## 🏗️ Architecture

```
┌───────────────────────────────────────────────────────┐
│                    Android App (Kotlin)                │
│   Jetpack Compose · MVVM · Hilt DI · Coroutines       │
├──────────────┬────────────────┬───────────────────────┤
│  Supabase    │   Firebase     │   OSMDroid / OSRM     │
│  (Postgres,  │  (Auth, FCM)   │  (Maps, Routing)      │
│   RLS, Edge  │                │                       │
│   Functions) │                │                       │
└──────────────┴────────────────┴───────────────────────┘
```

### Layers
```
presentation/
  ├── customer/        # CustomerHomeScreen, CustomerViewModel, MapBitmapHelper
  ├── barber/          # BarberDashboard, BarberViewModel
  ├── auth/            # PhoneRegistration, GoogleSignIn, CreateShop
  └── components/      # Shared UI components (KoupaBottomNav, etc.)
domain/
  ├── model/           # BarberShop, Appointment, AvailabilitySlot, User
  ├── repository/      # Interfaces
  └── usecase/         # Business logic
data/
  ├── repository/      # Supabase implementations
  ├── mapper/          # Data ↔ Domain mappers
  └── datasource/      # SupabaseClient, AuthDataSource
service/
  └── KoupaFirebaseMessagingService.kt
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Jetpack Compose, Material3, RTL |
| **Language** | Kotlin 1.9 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **Database** | Supabase (PostgreSQL + PostGIS) |
| **Auth** | Firebase Phone Auth + Google Sign-In |
| **Notifications** | Firebase Cloud Messaging v1 API |
| **Maps** | OSMDroid (OpenStreetMap) |
| **Routing** | OSRM Public API |
| **Image Loading** | Coil 2.5 |
| **Serialization** | kotlinx.serialization |
| **Backend Functions** | Supabase Edge Functions (Deno) |
| **Cron** | Supabase pg_cron |

---

## 🎨 Design Tokens

| Token | Color | Usage |
|-------|-------|-------|
| Primary Teal | `#1A7A78` | Buttons, active states, routing polyline |
| Primary Gold | `#E1A553` | Map markers border, accent elements |
| Secondary Slate | `#323E4B` | Dark backgrounds, card surfaces |
| Background Light | `#F3F5F7` | App background, list items |

**Font:** Cairo (Arabic-first, full RTL support)

---

## 🗄️ Supabase Schema

| Table | RLS | Description |
|-------|-----|-------------|
| `users` | ✅ | Customers & barbers (phone, role, fcm_token) |
| `barbershops` | ✅ | Shop profiles with PostGIS geography point |
| `appointments` | ✅ | Bookings with status lifecycle |
| `availability_slots` | ✅ | Per-shop per-day time slots |

### Edge Functions
| Function | Trigger | Action |
|----------|---------|--------|
| `get-nearby-shops` | HTTP | Radius search via PostGIS |
| `create-appointment` | HTTP | Atomic slot booking + FCM notify |
| `cancel-appointment` | HTTP | Release slot + FCM notify other party |
| `send-reminders` | pg_cron (every 5 min) | 30-min pre-appointment FCM reminders |

---

## 🚀 Getting Started (Development)

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- A Supabase project with the schema applied
- A Firebase project (`google-services.json`)

### 1. Clone
```bash
git clone https://github.com/samirsaadi610/koupa.git
cd koupa/android
```

### 2. Configure Secrets
Add to `android/local.properties`:
```
sdk.dir=<your Android SDK path>
```

The app reads `SUPABASE_URL` and `SUPABASE_ANON_KEY` from `build.gradle.kts` `buildConfigField`.

### 3. Build
```bash
./gradlew assembleRelease
```
APK will be at `app/build/outputs/apk/release/koupa-v1.0.0.apk`

---

## 📦 Releases

| Version | Date | Highlights |
|---------|------|------------|
| **v1.0.0** | 2026-03-22 | First public release — OSMDroid maps, OSRM routing, FCM v1 notifications, full booking flow |

---

## ⚖️ License

This project is licensed under a **Personal Use Only** license.
- ✅ Personal study and learning
- ❌ Commercial use prohibited
- ❌ Redistribution prohibited
- ❌ Resale prohibited

All intellectual property including the concept, design, and branding of "Koupa" is owned exclusively by the author.

See [LICENSE](./LICENSE) for full details.

---

## 👨‍💻 Author

**Samir Saadi**
📧 samirsaadi610@gmail.com

---

<div align="center">
  <sub>Built with ❤️ for the Algerian community · حُلِّق بأناقة 💈</sub>
</div>
