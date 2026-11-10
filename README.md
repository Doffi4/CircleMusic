# Circle Music

Open songs recognized by Circle to Search in your preferred music player's search.

[Download APK](https://github.com/Doffi4/CircleMusic/releases/latest) · Android 9+ · LSPosed

## Setup

1. Install the APK and enable Circle Music in LSPosed for **Google**.
2. Open Circle Music and choose an installed compatible player.
3. Grant root access to Circle Music when changing the player so Google can be stopped and restarted on its next launch.
4. Identify a song with Circle to Search. The recognized query opens in the selected player.

Installed music apps with a supported search action are listed. YouTube Music is detected separately through its exported search deep link: `https://music.youtube.com/search?q=...`, with the full query encoded as one parameter and the intent restricted to `com.google.android.apps.youtube.music`. This opens results without playing a track. Spotify keeps its existing search-action routing. ArchiveTune does not expose either supported search action in the tested version.

## Compatibility

The hook uses internal, obfuscated Google app methods. Supported mappings:

| Google version | Activity launcher | Recognition caller |
| --- | --- | --- |
| 17.70.11 | `fztx.c(Context, Intent)` | `dupd.invokeSuspend` |
| 17.73.15 | `gcxw.c(Context, Intent)` | `dwzy.invokeSuspend` |

17.70.11.ve.arm64 was inspected on a connected device. The 17.73.15 mapping is retained from the original release and was not re-tested on this device. Unknown versions are left untouched; expected signatures are checked before hooking. Other builds/architectures may differ even with the same version number. Google updates may break these internal entry points.

The recognition hook opens an explicit `Theme.NoDisplay` dispatcher activity in Circle Music. It reads the player's current setting in the module's own process, opens player search, and immediately finishes. It does not depend on Google being able to start a background settings provider: ColorOS can mark the module stopped after it is swiped from recents.

If the dispatcher cannot start, the hook leaves Google's original launch untouched. If the player is unavailable or redirection is off, the dispatcher opens Google's public `ACTION_WEB_SEARCH` entry point with the full original query and recognition extras, restricted to the Google package. Google's internal `GOOGLE_SEARCH` action cannot be launched directly by the module. The dispatcher does not accept arbitrary fallback actions or components. Logs report failures without search queries. Root access is used to stop Google after the player changes.

## Build

Requires JDK 17+, Android SDK 36 and Gradle wrapper. On Windows run `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`. The release APK is a debug-signed build, not a production-signed build.

Intent construction can also be checked on a connected Android device without launching any player:

```text
gradlew.bat :app:assembleDebugAndroidTest
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w dev.betterfy.circlemusic.test/dev.betterfy.circlemusic.PlayerSearchChecks
```

These checks validate full Unicode/special-character query encoding, package restriction, and both existing Spotify search actions. They do not replace actual recognition/player UI testing.
