package dev.betterfy.circlemusic;

/** Version-specific names: never guess obfuscated classes on an unknown Google build. */
final class GoogleHookTarget {
    final String launcher;
    final String recognition;

    private GoogleHookTarget(String launcher, String recognition) {
        this.launcher = launcher;
        this.recognition = recognition;
    }

    static GoogleHookTarget forVersion(String version) {
        if (matches(version, "17.70.11")) return new GoogleHookTarget("fztx", "dupd");
        if (matches(version, "17.73.15")) return new GoogleHookTarget("gcxw", "dwzy");
        return null;
    }

    private static boolean matches(String version, String base) {
        return version != null && (version.equals(base) || version.startsWith(base + "."));
    }
}
