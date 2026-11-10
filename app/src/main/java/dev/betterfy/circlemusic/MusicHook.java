package dev.betterfy.circlemusic;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MusicHook implements IXposedHookLoadPackage {
    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p) {
        if (!"com.google.android.googlequicksearchbox".equals(p.packageName)) return;
        try {
            // Package loading precedes Application creation. Read the APK version without
            // relying on a Context or hooking a framework method for every activity launch.
            Context systemContext = (Context)
                XposedHelpers.callMethod(XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("android.app.ActivityThread", p.classLoader),
                    "currentActivityThread"), "getSystemContext");
            String version = systemContext.getPackageManager().getPackageInfo(p.packageName, 0).versionName;
            GoogleHookTarget target = GoogleHookTarget.forVersion(version);
            if (target == null) {
                XposedBridge.log("Circle Music: unsupported Google " + version + "; keeping Google results");
                return;
            }
            XposedHelpers.findMethodExact(target.recognition, p.classLoader, "invokeSuspend", Object.class);
            Method launch = XposedHelpers.findMethodExact(target.launcher, p.classLoader,
                "c", Context.class, Intent.class);
            if (launch.getReturnType() != void.class || !Modifier.isStatic(launch.getModifiers())) {
                throw new IllegalStateException("Unexpected Google launch signature");
            }
            XposedBridge.hookMethod(launch, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) {
                    String stage = "recognition query";
                    try {
                        Intent original = (Intent) param.args[1];
                        if (original == null) return;
                        String query = original.getStringExtra(SearchManager.QUERY);
                        boolean recognized = false;
                        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
                            if (frame.getClassName().equals(target.recognition) && frame.getMethodName().equals("invokeSuspend")) { recognized = true; break; }
                        }
                        if (!Route.isRecognitionSearch(original.getAction(), query, recognized)) return;
                        Context context = (Context) param.args[0];
                        stage = "search dispatcher";
                        Intent dispatch = new Intent().setClassName("dev.betterfy.circlemusic",
                            "dev.betterfy.circlemusic.PlayerSearchActivity")
                            .putExtra("dev.betterfy.circlemusic.GOOGLE_INTENT", new Intent(original))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(dispatch);
                        param.setResult(null);
                        XposedBridge.log("Circle Music: dispatched recognized search");
                    } catch (RuntimeException e) {
                        // The dispatcher owns player/Google fallback after it starts.
                        // Do not log the recognized query.
                        XposedBridge.log("Circle Music: " + stage + " unavailable; keeping Google results: " + e.getClass().getSimpleName());
                    }
                }
            });
            XposedBridge.log("Circle Music: hooked Google " + version + " (" + target.launcher + ", " + target.recognition + ")");
        } catch (Throwable e) { XposedBridge.log("Circle Music: Google hook unavailable: " + e); }
    }
}

