package dev.betterfy.circlemusic;
final class SearchAction {
    static final String ANDROID = "android.intent.action.SEARCH";
    static final String GOOGLE = "com.google.android.gms.actions.SEARCH_ACTION";
    static String validated(String action) { return GOOGLE.equals(action) ? GOOGLE : ANDROID; }
}
