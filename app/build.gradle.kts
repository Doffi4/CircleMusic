plugins { id("com.android.application") }
android {
    namespace = "dev.betterfy.circlemusic"
    compileSdk = 36
    defaultConfig {
        applicationId = "dev.betterfy.circlemusic"; minSdk = 28; targetSdk = 35
        versionCode = 2; versionName = "0.1.1"
        testInstrumentationRunner = "dev.betterfy.circlemusic.PlayerSearchChecks"
    }
}
dependencies { compileOnly("de.robv.android.xposed:api:82"); testImplementation("junit:junit:4.13.2") }

