# Add project specific ProGuard rules here.
-keep class com.phishguard.ai.** { *; }
-keep class software.amazon.awssdk.** { *; }
-dontwarn software.amazon.awssdk.**
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*