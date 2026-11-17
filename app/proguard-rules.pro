# Keep Compose rules
-keepclassmembers class * extends androidx.compose.runtime.RecomposeScopeOwner {
    *;
}

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coroutines rules
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.coroutines.**
