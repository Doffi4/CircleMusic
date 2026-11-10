package dev.betterfy.circlemusic;
final class Route {
    static boolean shouldRedirect(String action, String query, String player, boolean fromRecognition) {
        return fromRecognition && "com.google.android.googlequicksearchbox.GOOGLE_SEARCH".equals(action)
            && query != null && !query.trim().isEmpty() && player != null && !player.trim().isEmpty();
    }
}
