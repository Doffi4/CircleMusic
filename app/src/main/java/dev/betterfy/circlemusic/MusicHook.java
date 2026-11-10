package dev.betterfy.circlemusic;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MusicHook implements IXposedHookLoadPackage {
    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p) {
        if (!"com.google.android.googlequicksearchbox".equals(p.packageName)) return;
        try {
            // shortcut: obfuscated Google 17.73.15 names change on updates, use signature discovery when supporting more versions.
            XposedHelpers.findAndHookMethod("gcxw", p.classLoader, "c", Context.class, Intent.class, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam param) {
                    Intent original = (Intent) param.args[1];
                    String query = original.getStringExtra(SearchManager.QUERY);
                    boolean recognized = false;
                    for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
                        if (frame.getClassName().equals("dwzy") && frame.getMethodName().equals("invokeSuspend")) { recognized = true; break; }
                    }
                    if (!recognized || query == null || query.trim().isEmpty()) return;
                    Context context = (Context) param.args[0];
                    try {
                        Bundle settings = context.getContentResolver().call(Uri.parse("content://dev.betterfy.circlemusic.settings"), "player", null, null);
                        String player = settings == null ? "" : settings.getString("package", "");
                        if (!Route.shouldRedirect(original.getAction(), query, player, recognized)) return;
                        Intent search = new Intent(SearchAction.validated(settings.getString("search_action"))).setPackage(player).putExtra(SearchManager.QUERY, query).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(search);
                        param.setResult(null);
                    } catch (RuntimeException ignored) { }
                }
            });
        } catch (Throwable e) { XposedBridge.log("Circle Music: Google hook unavailable: " + e); }
    }
}

