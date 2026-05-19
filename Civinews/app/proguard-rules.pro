# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Ignorar advertencias de clases faltantes de Google usadas por MapBox
-dontwarn com.google.android.gms.common.**
-dontwarn com.google.android.gms.location.**
-dontwarn com.google.android.gms.tasks.**
-dontwarn com.google.auto.value.**

# ==========================================
# REGLAS PARA LA CAPA DE RED (Retrofit & Gson)
# ==========================================
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# Mantener intactos tus modelos de datos y la API
# (Esto evita que cambie los nombres de las variables)
-keep class com.example.civinews.data.** { *; }