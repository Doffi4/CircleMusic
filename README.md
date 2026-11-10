# Circle Music

Open songs recognized by Circle to Search in your preferred music player's search.

[Download APK](https://github.com/Doffi4/CircleMusic/releases/latest) · Android 9+ · LSPosed

## Setup

1. Install the APK and enable Circle Music in LSPosed for **Google**.
2. Open Circle Music and choose an installed compatible player.
3. Grant root access to Circle Music when changing the player so Google can be stopped and restarted on its next launch.
4. Identify a song with Circle to Search. The recognized query opens in the selected player.

Only installed music apps that expose a supported search action are listed. Spotify was tested; other apps may handle search differently. ArchiveTune does not expose either supported search action in the tested version.

## Compatibility

The hook uses internal, obfuscated Google app methods verified with version **17.73.15**. Google updates may break it; if the hook cannot redirect, Google search remains available. Root access permits the app to run privileged commands; it is used to stop Google after the player changes.

## Build

Requires JDK 17+, Android SDK 36 and Gradle wrapper. On Windows run `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`. The release APK is a debug-signed build, not a production-signed build.
