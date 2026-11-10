plugins { id("com.android.application") }
android {
    namespace = "dev.betterfy.circlemusic"
    compileSdk = 36
    defaultConfig { applicationId = "dev.betterfy.circlemusic"; minSdk = 28; targetSdk = 35; versionCode = 1; versionName = "0.1" }
}
dependencies { compileOnly("de.robv.android.xposed:api:82"); testImplementation("junit:junit:4.13.2") }

