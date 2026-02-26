# Checkers App for Android

This is a modern Android Checkers application built with Kotlin and Jetpack Compose.

## Features
- **Pass & Play**: Two players on the same device.
- **Single Player vs CPU**: Play against a Minimax AI with Alpha-Beta pruning.
- **Configurable Settings**: 
  - Change AI Difficulty (Easy, Medium, Hard).
  - Toggle "Forced Jumps" rule on or off.
- **Monetization**: Google AdMob Banner Integration.

## Preparing for Production Release (AdMob)

> [!WARNING]
> The app currently uses **Google Test Ad IDs**. If you publish the app to the Google Play Store with these IDs, the ads will not generate revenue. 
> 
> However, **do not use your real Ad IDs while developing or testing the app on your own devices**, as clicking your own ads will result in an AdSense account suspension.

Before building the production release APK/AAB for the Play Store, you must update the AdMob strings to match your real account IDs.

1. Log into your Google AdMob Dashboard.
2. Register this app in the dashboard to get your **App ID**.
3. Create a new "Banner" Ad Unit to get your **Ad Unit ID**.
4. Open the `app/src/main/res/values/strings.xml` file in Android Studio.
5. Replace the test values with your real values:

```xml
    <!-- Replace this with your actual AdMob App ID -->
    <string name="admob_app_id">ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY</string>
    
    <!-- Replace this with your actual AdMob Banner Ad Unit ID -->
    <string name="admob_banner_id">ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ</string>
```

Once those two strings are updated, both the `AndroidManifest.xml` and the `BannerAd.kt` composable will automatically use the real IDs for production.
