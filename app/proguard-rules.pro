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

# 不混淆继承自xposed的类名
-keep public class * implements de.robv.android.xposed.IXposedHookLoadPackage

# 不混淆的类和方法
-keep class com.linsheng.FATJS.node.TaskBase {
    *;
}
-keep class com.linsheng.FATJS.node.UiObject {
    *;
}
-keep class com.linsheng.FATJS.node.UiSelector {
    *;
}
-keep class com.linsheng.FATJS.node.UiCollection {
    *;
}
-keep class com.linsheng.FATJS.node.AccUtils {
    *;
}
-keep class com.linsheng.FATJS.node.App {
    *;
}
-keep class com.linsheng.FATJS.config.GlobalVariableHolder {
    *;
}
-keep class com.linsheng.FATJS.okhttp3.HttpUtils {
    *;
}
-keep class com.linsheng.FATJS.okhttp3.WebSocketUtils {
    *;
}
-keep class android.content.Intent {
    *;
}
-keep class com.linsheng.FATJS.ntptime.NtpService {
    *;
}
-keep class com.linsheng.FATJS.findColor.ScreenLib {
    *;
}

#-keep public class * extends android.app.Fragment

# 不混淆C/C++底层库暴露的方法
# see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#################
#项目自定义混淆配置
#################
-classobfuscationdictionary ./pro-android.txt
-packageobfuscationdictionary ./pro-android.txt
-obfuscationdictionary ./pro-android.txt