package dev.betterfy.circlemusic;
import org.junit.Test;
import static org.junit.Assert.*;
public class RouteTest {
    @Test public void onlyRecognizedSongsWithAChosenPlayerAreRedirected() {
        assertTrue(Route.shouldRedirect("com.google.android.googlequicksearchbox.GOOGLE_SEARCH", "Artist Song", "com.spotify.music", true));
        assertFalse(Route.shouldRedirect("com.google.android.googlequicksearchbox.GOOGLE_SEARCH", " ", "com.spotify.music", true));
        assertFalse(Route.shouldRedirect("com.google.android.googlequicksearchbox.GOOGLE_SEARCH", "Artist Song", "", true));
        assertFalse(Route.shouldRedirect("com.google.android.googlequicksearchbox.GOOGLE_SEARCH", "Artist Song", "com.spotify.music", false));
        assertFalse(Route.shouldRedirect("android.intent.action.VIEW", "Artist Song", "com.spotify.music", true));
    }
}
