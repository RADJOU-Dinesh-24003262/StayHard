# ✅ Règles ProGuard pour l'application StayHard

# ══════════════════════════════════════════════════════════════
# RÈGLES GÉNÉRALES
# ══════════════════════════════════════════════════════════════

# Garder les numéros de ligne pour les stack traces
-keepattributes SourceFile,LineNumberTable

# Renommer les fichiers sources en "SourceFile"
-renamesourcefileattribute SourceFile

# ══════════════════════════════════════════════════════════════
# MODÈLES DE DONNÉES (Serialization Gson)
# ══════════════════════════════════════════════════════════════

# Garder tous les modèles pour Gson
-keep class com.example.stayhard.app.model.** { *; }
-keepclassmembers class com.example.stayhard.app.model.** { *; }

# Règles Gson générales
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-keep class com.google.gson.** { *; }
#-keep class sun.misc.Unsafe { *; }

# ══════════════════════════════════════════════════════════════
# RETROFIT & OKHTTP
# ══════════════════════════════════════════════════════════════

# Retrofit
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ══════════════════════════════════════════════════════════════
# KOTLIN
# ══════════════════════════════════════════════════════════════

-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ══════════════════════════════════════════════════════════════
# ANDROIDX & ANDROID
# ══════════════════════════════════════════════════════════════

-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# LiveData
-keep class * extends androidx.lifecycle.LiveData {
    *;
}

# ══════════════════════════════════════════════════════════════
# OPTIMISATIONS
# ══════════════════════════════════════════════════════════════

# Supprimer les logs en production
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ══════════════════════════════════════════════════════════════
# RÈGLES SPÉCIFIQUES À L'APP
# ══════════════════════════════════════════════════════════════

# Garder les BroadcastReceivers
-keep class * extends android.content.BroadcastReceiver {
    *;
}

# Garder les classes avec des méthodes natives
-keepclasseswithmembernames class * {
    native <methods>;
}