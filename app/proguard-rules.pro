# Keep data classes that are serialized
-keepclassmembers class * {
    *** Companion;
}

# Keep Room entities
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }

# Keep DataStore classes
-keep class * extends androidx.datastore.core.Serializer { *; }

# Keep Hilt components
-keep class * implements dagger.Module { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Remove logging in release builds
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
