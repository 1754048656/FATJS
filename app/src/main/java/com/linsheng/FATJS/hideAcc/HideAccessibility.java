package com.linsheng.FATJS.hideAcc;

import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;
import android.app.Application;
import android.content.ContentResolver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;

import de.robv.android.fposed.FC_MethodHook;
import de.robv.android.fposed.FC_MethodReplacement;
import de.robv.android.fposed.FposedBridge;
import de.robv.android.fposed.FposedHelpers;
import de.robv.android.fposed.IFposedHookLoadPackage;
import de.robv.android.fposed.IFposedHookZygoteInit;
import de.robv.android.fposed.callbacks.FC_LoadPackage;

public class HideAccessibility implements IFposedHookLoadPackage, IFposedHookZygoteInit {
    private final String app_package_name = "com.smile.gifmaker";
    private static ClassLoader mClassLoader;
    private static boolean loaded = false;
    @Override
    public void handleLoadPackage(FC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        preTask(loadPackageParam);
    }

    private void preTask(FC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(tag, "Loaded app: " + lpparam.packageName);
        if (lpparam.packageName.contains(app_package_name)) {
            Class<?> ActivityThread = FposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader);
            FposedBridge.hookAllMethods(ActivityThread, "performLaunchActivity", new FC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Application mInitialApplication = (Application) FposedHelpers.getObjectField(param.thisObject, "mInitialApplication");
                    mClassLoader = mInitialApplication.getClassLoader();
                    Log.i(tag, "found classload is => " + mClassLoader.toString());
                    mainHook();
                }
            });
        }
    }

    private void mainHook() {
        if (!loaded) {
            loaded = true;
            try {
                // 过检测
                ByPassFposedDetector();
                ByPassAccDetector();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void ByPassAccDetector() {
        FposedHelpers.findAndHookMethod("android.provider.Settings$Secure", mClassLoader, "getString", ContentResolver.class, String.class, new FC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam2) throws Throwable {
                if (Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES.equals(methodHookParam2.args[1])) {
                    methodHookParam2.setResult("");
                }
            }
        });
        FposedHelpers.findAndHookMethod("android.provider.Settings$Secure", mClassLoader, "getInt", ContentResolver.class, String.class, new FC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam2) throws Throwable {
                if (Settings.Secure.ACCESSIBILITY_ENABLED.equals(methodHookParam2.args[1])) {
                    methodHookParam2.setResult(0);
                }
            }
        });
    }

    private boolean doNotHack(Throwable throwable) {
        if (throwable.getStackTrace().length >= 3) {
            StackTraceElement stackTraceElement = throwable.getStackTrace()[3];
            return stackTraceElement.getClassName().startsWith("android.")
                    || stackTraceElement.getClassName().startsWith("androidx.")
                    || stackTraceElement.getClassName().startsWith("com.android.")
                    || stackTraceElement.getClassName().startsWith("org.chromium.content.browser.");
        }
        return false;
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        FposedBridge.log("initZygote: " + startupParam.modulePath);
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "getEnabledAccessibilityServiceList",
                int.class,
                FC_MethodReplacement.returnConstant(Collections.emptyList())
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "isEnabled",
                new FC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Throwable throwable = new Throwable();
                        if (doNotHack(throwable)) return;
                        param.setResult(false);
                    }
                }
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "isTouchExplorationEnabled",
                new FC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Throwable throwable = new Throwable();
                        if (doNotHack(throwable)) return;
                        param.setResult(false);
                    }
                }
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addAccessibilityStateChangeListener",
                AccessibilityManager.AccessibilityStateChangeListener.class,
                FC_MethodReplacement.returnConstant(true)
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addAccessibilityStateChangeListener",
                AccessibilityManager.AccessibilityStateChangeListener.class,
                Handler.class,
                FC_MethodReplacement.returnConstant(null)
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addTouchExplorationStateChangeListener",
                AccessibilityManager.TouchExplorationStateChangeListener.class,
                FC_MethodReplacement.returnConstant(true)
        );
        FposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addTouchExplorationStateChangeListener",
                AccessibilityManager.TouchExplorationStateChangeListener.class,
                Handler.class,
                FC_MethodReplacement.returnConstant(null)
        );
    }

    private void ByPassFposedDetector() {
        Log.i(tag, "ByPassFposedDetector");

        // 绕过堆栈检测(getClassName)
        FposedHelpers.findAndHookMethod(StackTraceElement.class, "getClassName", new FC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = (String) param.getResult();
                if (result != null){
                    if (
                            result.contains("LFPHooker_") ||
                            result.contains("org.lsposed.lspd.impl.") ||
                            result.contains("de.robv.android.fposed.") ||
                            result.contains("posedBridge")
                    ) {
                        Log.i(tag,"替换了，字符串名称 " + result);
                        param.setResult("f");
                    }
                    if(result.contains("com.android.internal.os.ZygoteInit")){
                        Log.i(tag,"StackTrace ZygoteInit 找到了 " + result);
                        param.setResult("n");
                    }
                }
                super.afterHookedMethod(param);
            }
        });

        // 绕过堆栈检测(getMethodName)
        FposedHelpers.findAndHookMethod(StackTraceElement.class, "getMethodName", new FC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = (String) param.getResult();
                if (result != null){
                    if (
                            result.contains("beforeHookedMethod") ||
                                    result.contains("afterHookedMethod")
                    ) {
                        Log.i(tag,"替换了，字符串名称 " + result);
                        param.setResult("bM");
                    }
                }
                super.afterHookedMethod(param);
            }
        });

        // 绕过maps检测
        FposedHelpers.findAndHookConstructor("java.io.FileReader", mClassLoader, String.class, new FC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String arg0 = (String) param.args[0];
                if(arg0.toLowerCase().contains("/proc/")){
                    Log.i(tag,"FileReader " + arg0);

                    // 创建一个假的 BufferedReader 对象并返回
                    String fakeContent = "蜘蛛侠";
                    BufferedReader fakeReader = new BufferedReader(new StringReader(fakeContent));
                    param.setResult(fakeReader);
                }
            }
        });
    }
}
