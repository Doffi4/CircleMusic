package dev.betterfy.circlemusic;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;

final class PlayerSearch {
    static final String YOUTUBE_MUSIC = "com.google.android.apps.youtube.music";

    static Intent create(String player, String action, String query) {
        Intent intent;
        if (YOUTUBE_MUSIC.equals(player)) {
            // The exported deep-link entry point handles new queries even when MusicActivity
            // already has a task. Do not use MEDIA_PLAY_FROM_SEARCH or a watch URL.
            Uri uri = new Uri.Builder().scheme("https").authority("music.youtube.com")
                .appendPath("search").appendQueryParameter("q", query).build();
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else {
            intent = new Intent(SearchAction.validated(action)).putExtra(SearchManager.QUERY, query);
        }
        // An unavailable player must throw, so the hook can retain the Google result;
        // never let a browser or a different app consume the intent.
        return intent.setPackage(player).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
