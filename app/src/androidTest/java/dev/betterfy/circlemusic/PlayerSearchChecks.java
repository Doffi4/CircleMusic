package dev.betterfy.circlemusic;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/** Dependency-free device checks using real Android Intent/Uri implementations. */
public class PlayerSearchChecks extends Instrumentation {
    @Override public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        start();
    }

    @Override public void onStart() {
        Bundle result = new Bundle();
        try {
            String query = "Мумий Тролль — Владивосток 2000 & live + remix / #?% 🎵";
            Intent youtube = PlayerSearch.create(PlayerSearch.YOUTUBE_MUSIC, SearchAction.ANDROID, query);
            check(Intent.ACTION_VIEW.equals(youtube.getAction()), "YouTube search action");
            check(PlayerSearch.YOUTUBE_MUSIC.equals(youtube.getPackage()), "YouTube package restriction");
            check("https".equals(youtube.getData().getScheme()), "HTTPS scheme");
            check("music.youtube.com".equals(youtube.getData().getHost()), "YouTube host");
            check("/search".equals(youtube.getData().getPath()), "Search results path");
            check(query.equals(youtube.getData().getQueryParameter("q")), "Full query round trip");
            check(youtube.getData().getQueryParameterNames().size() == 1, "No injected URL parameters");
            for (String action : new String[] {SearchAction.ANDROID, SearchAction.GOOGLE}) {
                Intent spotify = PlayerSearch.create("com.spotify.music", action, query);
                check(action.equals(spotify.getAction()), "Preserve Spotify action");
                check("com.spotify.music".equals(spotify.getPackage()), "Preserve Spotify package");
                check(query.equals(spotify.getStringExtra(SearchManager.QUERY)), "Preserve full Spotify query");
                check(spotify.getData() == null, "No deep link for Spotify");
                check((spotify.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0, "New task flag");
            }
            Intent legacy = PlayerSearch.create("com.spotify.music", null, query);
            check(SearchAction.ANDROID.equals(legacy.getAction()), "Old preferences still work");
            Intent original = new Intent("com.google.android.googlequicksearchbox.GOOGLE_SEARCH")
                .setClassName("untrusted.package", "UntrustedActivity")
                .putExtra(SearchManager.QUERY, query).putExtra("search-stick", "recognition metadata");
            Intent fallback = PlayerSearchActivity.googleFallback(original);
            check(PlayerSearchActivity.GOOGLE.equals(fallback.getPackage()), "Fallback restricted to Google");
            check(fallback.getComponent() == null, "Ignore externally supplied component");
            check(Intent.ACTION_WEB_SEARCH.equals(fallback.getAction()), "Use public Google entry point");
            check(query.equals(fallback.getStringExtra(SearchManager.QUERY)), "Full fallback query");
            check("recognition metadata".equals(fallback.getStringExtra("search-stick")), "Keep recognition metadata");
            check(original.getComponent() != null, "Do not mutate original intent");
            result.putString("stream", "PlayerSearch: 24 assertions passed; no activity launched.\n");
            finish(Activity.RESULT_OK, result);
        } catch (Throwable error) {
            result.putString("stream", "PlayerSearch FAILED: " + error + "\n");
            finish(Activity.RESULT_CANCELED, result);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
