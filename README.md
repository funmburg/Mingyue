# Mingyue

Personal planner. The app itself is one HTML file, `app/mingyue.html`, the same file that runs as the claude.ai artifact.
This repo wraps it into an Android app with [Capacitor](https://capacitorjs.com).

## Getting the APK

Every push to `main` builds the APK on GitHub Actions. Open **Actions → Android APK → latest run → Artifacts → mingyue-apk**, download, unzip, and install the `.apk` on the phone (allow installing from unknown sources once).

## Updating the app

1. Replace `app/mingyue.html` with the new version of the artifact source.
2. Push to `main`. The next APK has the change.

## Local build

```sh
npm ci
npm run build:web
npx cap add android   # first time only; android/ is generated, not committed
npm run assets
npx cap sync android
cd android && ./gradlew assembleDebug
```

`ci/debug.keystore` is a fixed debug key (password `android`), so each new APK installs over the previous one.

## Known limits of the APK for now

- Data stays on the phone (browser storage). claude.ai's database and sync are not available outside claude.ai.
- Google Calendar works only in the claude.ai version. The APK needs its own Google sign-in, planned next.
