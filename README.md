<div align="center">

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120" height="120" alt="Koupa Logo"/>

# كوبا | Koupa

### منصة حجز صالونات الحلاقة الجزائرية

[![Release](https://img.shields.io/github/v/release/sam22ir/Koupa-Barber-Booking?color=0d9488&label=إصدار&style=for-the-badge)](https://github.com/sam22ir/Koupa-Barber-Booking/releases)
[![License](https://img.shields.io/badge/رخصة-MIT-gold?style=for-the-badge)](LICENSE)
[![Platform](https://img.shields.io/badge/منصة-Android%206.0%2B-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)

**[⬇️ تحميل التطبيق](https://github.com/sam22ir/Koupa-Barber-Booking/releases/latest)** | **[🐛 الإبلاغ عن مشكلة](https://github.com/sam22ir/Koupa-Barber-Booking/issues)**

</div>

---

## 📸 لقطات الشاشة

> قريباً...

---

## ✨ المميزات

| للزبون 👤 | للحلاق ✂️ |
|-----------|-----------|
| البحث عن صالونات قريبة | لوحة تحكم احترافية |
| حجز موعد بنقرة واحدة | إدارة جدول المواعيد |
| عرض أوقات متاحة | استقبال إشعارات فورية |
| تتبع حالة الحجز | إحصائيات يومية |
| إلغاء الحجز بسهولة | التحكم في الأوقات المتاحة |

---

## 🏗️ المعمارية والتقنيات

```
Koupa
├── 🎨 UI Layer        → Jetpack Compose (Material 3) + Hilt Navigation
├── 🧠 Domain Layer    → Use Cases + Repositories (Clean Architecture)
├── 💾 Data Layer      → Supabase (PostgreSQL) + Firebase Auth
└── 🔔 Services        → Firebase Cloud Messaging (FCM)
```

### المكتبات الرئيسية

| الفئة | المكتبة |
|-------|---------|
| **UI** | Jetpack Compose · Material 3 · Coil |
| **Backend** | Supabase (Postgrest · Realtime · Auth) |
| **Auth** | Firebase Phone Authentication |
| **DI** | Hilt (KSP) |
| **Networking** | Ktor Android |
| **Navigation** | Navigation Compose |
| **Async** | Kotlin Coroutines · Flow |

---

## 🚀 كيفية البناء محلياً

### المتطلبات
- Android Studio Hedgehog أو أحدث
- JDK 17
- Android SDK 34

### الخطوات

```bash
# 1. استنساخ المستودع
git clone https://github.com/sam22ir/Koupa-Barber-Booking.git
cd Koupa-Barber-Booking

# 2. إضافة google-services.json في app/
# (احصل عليه من Firebase Console)

# 3. البناء
./gradlew assembleDebug

# 4. التثبيت على الجهاز
./gradlew installDebug
```

---

## 🗄️ إعداد Supabase

التطبيق يستخدم [Supabase](https://supabase.com) للقاعدة البيانات. الجداول الرئيسية:

| الجدول | الوصف |
|--------|-------|
| `users` | معلومات المستخدمين (زبائن وحلاقين) |
| `barbershops` | بيانات الصالونات |
| `availability_slots` | جدول المواعيد المتاحة |
| `appointments` | الحجوزات المنجزة |

---

## 🔧 الإعداد المطلوب

بعد التثبيت، يجب:
1. **Firebase Console** → تفعيل Phone Authentication
2. **Firebase Console** → إضافة بصمة SHA-1 للتطبيق
3. **Supabase** → إنشاء الجداول وتفعيل Row Level Security (RLS)

---

## 📄 الرخصة

هذا المشروع مرخص تحت [رخصة MIT](LICENSE).

---

<div align="center">

صُنع بـ ❤️ في **الجزائر** | Made with ❤️ in **Algeria** 🇩🇿

</div>
