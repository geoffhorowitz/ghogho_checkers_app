# Ghogho Checkers Deployment Guide

This document contains necessary information for releasing **Ghogho Checkers** to the Google Play Store.

## App Details
- **App Name (Play Console):** Ghogho Checkers
- **Package Name:** `com.ghogho.checkers`
- **Current Version:** 1.0.0

## Release Information

### Release Name
`1.0.0 (Initial Release)`

### Release Notes
```text
Welcome to the initial release of Ghogho Checkers!
- Play classic Checkers against the CPU with Easy, Medium, and Hard difficulty levels.
- Enjoy local multiplayer games with a friend on the same device.
- Clean, modern UI with smooth gameplay.
- Optional forced jump rules.
```

## Build and Deployment Steps
1. **Configure AdMob IDs**:
   - Before building the release APK/Bundle, replace the default AdMob TEST IDs with your real AdMob IDs.
   - Open or create `local.properties` in the project root directory.
   - Add/update the following lines with your production IDs:
     ```properties
     ADMOB_APP_ID=ca-app-pub-xxxxxxxxxxxxxxxx~xxxxxxxxxx
     ADMOB_BANNER_ID=ca-app-pub-xxxxxxxxxxxxxxxx/xxxxxxxxxx
     ```
   *(Note: `local.properties` is git-ignored to prevent pushing these IDs to your public repository).*

2. **Generate Signed Bundle**:
   - In Android Studio, go to `Build > Generate Signed Bundle / APK...`
   - Select `Android App Bundle`.
   - Provide the keystore path and credentials (ensure you back up your keystore file securely).
   - Select the `release` build variant and click `Finish`.

3. **Upload to Play Console**:
   - Navigate to the [Google Play Console](https://play.google.com/console).
   - Select "Ghogho Checkers".
   - Go to `Release > Production` (or Internal Testing/Closed Testing if preferred).
   - Create a new release.
   - Upload the generated `.aab` (Android App Bundle) file located in `app/release/app-release.aab`.
   - Enter the Release Name and Release Notes above.
   - Save, Review, and Roll out the release!

## Future Updates
- Remember to increment the `versionCode` and update the `versionName` in `app/build.gradle.kts` before generating a new release bundle.

## AdMob App-Ads.txt Setup
To verify the app with AdMob and remove the "unreviewed" status:
1. Ensure you have a developer website listed in the **Google Play Console** (under Store presence > Main store listing).
2. Host an `app-ads.txt` file at the root of that domain (e.g., `https://your-website.com/app-ads.txt`).
3. The file must contain exactly your AdMob publisher snippet:
   ```text
   google.com, pub-1011368772130588, DIRECT, f08c47fec0942fa0
   ```
*(If you don't have a website, you can easily host this for free using GitHub Pages).*
