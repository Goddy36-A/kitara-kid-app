# Kitara Kid

A single-artist Android music app built with Kotlin + Jetpack Compose + Media3 (ExoPlayer).

## What's here
- **Song list** \u2192 tap a track to play it in-app (if hosted) or jump into the full player.
- **Full player** \u2192 seek bar, play/pause, and an "Also available on" row linking out to YouTube, Mdundo, Tubidy, and Muzbox.
- **Mini player** \u2192 persists at the bottom while browsing the list.

## Add your real tracks
Open `app/src/main/java/com/kitarakid/app/data/SongRepository.kt` and replace the placeholder entries:
- `audioUrl` \u2014 a direct, publicly-reachable MP3 link (Firebase Storage, S3, your own host). This is what plays in-app.
- `coverUrl` \u2014 your artwork.
- `youtubeUrl` / `mdundoUrl` / `tubidyUrl` / `muzboxUrl` \u2014 links to that track on each platform. Leave any as `null` if not applicable.

If a song has no `audioUrl`, the app skips in-app playback and just shows the external links.

## Building the APK via GitHub Actions
This repo includes `.github/workflows/build-apk.yml`. On every push to `main` (or manually via **Actions \u2192 Build APK \u2192 Run workflow**), it:
1. Sets up JDK 17 + Android SDK
2. Runs `gradle assembleDebug`
3. Uploads `app-debug.apk` as a downloadable workflow artifact

Go to the **Actions** tab after pushing, open the latest run, and download the APK from the **Artifacts** section.

## Building locally instead
Open the project root in Android Studio (Koala or newer) and run it, or from the command line:
```
./gradlew assembleDebug
```
(You'll need to generate the Gradle wrapper once with `gradle wrapper` if you don't already have `gradlew` in this folder \u2014 Android Studio will do this automatically on first open.)

## Next steps toward a real release
- Swap the placeholder `picsum.photos` cover art for your real artwork.
- Add a proper app icon (`res/mipmap-*/ic_launcher`).
- Move `SongRepository` from a hardcoded list to Firestore or a JSON file on your own server, so you can update tracks without shipping a new app version.
- Set up a signing key + `assembleRelease` job once you're ready to distribute beyond your own device.
