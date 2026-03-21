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
