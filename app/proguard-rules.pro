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

#
## ical4j also contains groovy code which is not used in android
-dontwarn groovy.**
-dontwarn org.codehaus.groovy.**
-dontwarn org.apache.commons.logging.**

-dontnote com.google.vending.**
-dontnote com.android.vending.licensing.**

## log4j: remove net.fortuna.ical4j.util.JCacheTimeZoneCache.** that requires javax.cache.**
## use MapTimeZoneCache instead
-assumenosideeffects class net.fortuna.ical4j.util.JCacheTimeZoneCache
-assumenosideeffects class javax.cache.Cache
-assumenosideeffects class javax.cache.CacheManager
-assumenosideeffects class javax.cache.Caching
-assumenosideeffects class javax.cache.configuration.Configuration
-assumenosideeffects class javax.cache.configuration.MutableConfiguration
-assumenosideeffects class javax.cache.spi.CachingProvider
-keep class net.fortuna.ical4j.util.MapTimeZoneCache

###################
# Get rid of #can't find referenced method in library class java.lang.Object# warnings for clone() and finalize()
# Warning: net.fortuna.ical4j.model.CalendarFactory: can't find referenced method 'void finalize()' in library class java.lang.Object
# Warning: net.fortuna.ical4j.model.ContentBuilder: can't find referenced method 'java.lang.Object clone()' in library class java.lang.Object
# for details see http://stackoverflow.com/questions/23883028/how-to-fix-proguard-warning-cant-find-referenced-method-for-existing-methods
-dontwarn net.fortuna.ical4j.model.**

###############
# I use proguard only to remove unused stuff and to keep the app small.
# I donot want to obfuscate (rename packages, classes, methods, ...) since this is open source
-keepnames class ** { *; }
-keepnames interface ** { *; }
-keepnames enum ** { *; }
