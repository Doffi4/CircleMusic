package dev.betterfy.circlemusic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;

public class SettingsProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }
    @Override public Bundle call(String method, String arg, Bundle extras) {
        String[] packages = getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        boolean allowed = false;
        if (packages != null) for (String name : packages) if (name.equals("com.google.android.googlequicksearchbox")) allowed = true;
        if (!allowed || !"player".equals(method)) throw new SecurityException();
        Bundle result = new Bundle();
        result.putString("package", getContext().getSharedPreferences("settings", 0).getString("player", ""));
        result.putString("search_action", SearchAction.validated(getContext().getSharedPreferences("settings", 0).getString("search_action", null)));
        return result;
    }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] args) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new UnsupportedOperationException(); }
}

