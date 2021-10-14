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
-dontwarn sun.misc.Perf

-dontnote com.google.vending.**
-dontnote com.android.vending.licensing.**

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
