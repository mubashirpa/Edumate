# Required for Firebase

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Firestore test rules

-keepattributes SourceFile,LineNumberTable
-dontwarn org.xmlpull.v1.**
-dontnote org.xmlpull.v1.**
-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }
-keep class com.google.firebase.auth.** {*;}

# Required for Firebase database and firestore

-keepclassmembers class edumate.app.data.remote.dto.** { *; }
-keepclassmembers class edumate.app.domain.model.** { *; }