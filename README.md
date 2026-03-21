<div align="center">

# 💈 Koupa (كوبا)
### Premium Barber Booking in Algeria

![Android](https://img.shields.io/badge/Android-Only-green?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-purple?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack-Compose-blue?logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth+FCM-orange?logo=firebase&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-green?logo=supabase&logoColor=white)

**Book your favorite barber with a tap — cash on arrival in Algerian Dinars (DZD)**

📱 Android Exclusive • 🇩🇿 Algeria Market • 💵 Cash Only (DZD) • 🌐 Arabic + English

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📞 **Phone Auth** | Secure OTP verification via Firebase |
| 🔍 **Smart Search** | Find nearby barbershops using GPS |
| 📅 **Easy Booking** | Book appointments in 3 simple steps |
| 🔔 **Push Notifications** | Real-time booking updates via FCM |
| 🌍 **Bilingual** | Arabic (RTL) + English support |
| 🏪 **Dual Role** | Customer & Barber interfaces |
| 📍 **58 Wilayas** | Coverage across all Algerian provinces |
| 💵 **Cash Payment** | Pay at the shop — no online payment |

---

## 🛠️ Tech Stack

### Backend
- **Supabase** — PostgreSQL 15 database with Row Level Security
- **Edge Functions** — Serverless functions for bookings
- **PostGIS** — Geospatial queries for nearby search

### Authentication & Notifications
- **Firebase Auth** — Phone OTP verification
- **FCM** — Push notifications for bookings

### Android
- **Kotlin** — Modern Android development language
- **Jetpack Compose** — Declarative UI toolkit
- **MVVM + Clean Architecture** — Scalable code structure
- **Hilt** — Dependency injection
- **Coroutines + Flow** — Async programming

### Design
- **Google Stitch MCP** — AI-generated UI screens
- **Cairo + Montserrat** — Arabic/Latin typography
- **Material Design 3** — Modern Android design

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌───────────┐ │
│  │ Auth VM │  │Customer │  │ Barber  │  │Navigation │ │
│  │         │  │  Home   │  │Dashboard│  │   Graph   │ │
│  └─────────┘  └─────────┘  └─────────┘  └───────────┘ │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                       │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐                │
│  │  Models │  │Use Cases│  │Interface│                │
│  │         │  │         │  │Repos    │                │
│  └─────────┘  └─────────┘  └─────────┘                │
├─────────────────────────────────────────────────────────┤
│                       Data Layer                        │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │
│  │Firebase │  │Supabase │  │  Mappers│  │  Repos  │  │
│  │  Auth   │  │   API   │  │         │  │   Impl  │  │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Database Schema

| Table | Description |
|-------|-------------|
| `users` | Customers & barbers with phone auth |
| `barbershops` | Shop details with GPS coordinates |
| `appointments` | Bookings with status tracking |
| `availability_slots` | Barber's weekly time grid |

### Edge Functions
- `get-nearby-shops` — GPS-based barbershop search
- `create-appointment` — Atomic slot booking
- `cancel-appointment` — Booking cancellation

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1+)
- JDK 17+
- Android SDK 34
- Firebase project with Phone Auth enabled

### Setup
1. Clone the repository
```bash
git clone https://github.com/sam22ir/Koupa-Barber-Booking.git
```

2. Open in Android Studio

3. Configure Firebase
   - Add your `google-services.json` to `app/` directory

4. Build and Run
```bash
./gradlew assembleDebug
```

5. Install APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Team

Built with ❤️ for Algeria 🇩🇿

---

<div align="center">

**© 2026 Koupa — Premium Barber Booking**

</div>
