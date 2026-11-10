package dev.betterfy.circlemusic;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/** Explicit, no-UI entry point also works after the module was removed from recents. */
public class PlayerSearchActivity extends Activity {
    static final String ORIGINAL = "dev.betterfy.circlemusic.GOOGLE_INTENT";
    static final String GOOGLE = "com.google.android.googlequicksearchbox";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        try {
            Intent original = getIntent().getParcelableExtra(ORIGINAL);
            if (original == null) return;
            String query = original.getStringExtra(SearchManager.QUERY);
            // This exported entry point only accepts a Google search. Never forward
            // arbitrary components, packages or intent actions supplied by a caller.
            if (!Route.isRecognitionSearch(original.getAction(), query, true)) return;
            Intent fallback = googleFallback(original);
            try {
                SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
                String player = settings.getString("player", "");
                if (Route.shouldRedirect(original.getAction(), query, player, true)) {
                    startActivity(PlayerSearch.create(player, settings.getString("search_action", null), query));
                    Log.i("CircleMusic", "Opened player search: " + player);
                    return;
                }
            } catch (RuntimeException e) {
                Log.w("CircleMusic", "Player/settings unavailable; opening Google: " + e.getClass().getSimpleName());
            }
            startActivity(fallback);
        } catch (RuntimeException e) {
            Log.w("CircleMusic", "Search dispatch failed: " + e.getClass().getSimpleName());
        } finally {
            finish();
        }
    }

    static Intent googleFallback(Intent original) {
        Intent fallback = new Intent(original);
        fallback.setSelector(null);
        // GOOGLE_SEARCH is an internal action with no exported intent filter. The
        // public WEB_SEARCH entry point accepts the same query extra from our UID.
        return fallback.setAction(Intent.ACTION_WEB_SEARCH).setDataAndType(null, null)
            .setComponent(null).setPackage(GOOGLE).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
