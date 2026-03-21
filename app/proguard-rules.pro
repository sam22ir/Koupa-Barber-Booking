# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Supabase classes
-keep class io.github.jan.supabase.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }

# Keep Ktor classes
-keep class io.ktor.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Suppress warnings for missing Java desktop/server classes not available on Android
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.**

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Hilt generated components
-keepclassmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Keep model/data classes for Supabase serialization
-keep class com.koupa.barberbooking.domain.model.** { *; }
-keep class com.koupa.barberbooking.data.model.** { *; }
