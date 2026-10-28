// Android 12+ always shows its own launch screen (app icon in a circle).
// Make it a blank screen in the splash colour so the app's own splash with Migo is the one people see.
import { readFileSync, writeFileSync, copyFileSync, mkdirSync } from "node:fs";

const res = "android/app/src/main/res";
for (const f of ["drawable/msplash_blank.xml", "values/msplash.xml", "values-night/msplash.xml"]) {
  mkdirSync(`${res}/${f.split("/")[0]}`, { recursive: true });
  copyFileSync(`ci/native/res/${f}`, `${res}/${f}`);
}
const styles = `${res}/values/styles.xml`;
const xml = readFileSync(styles, "utf8");
const launch = /(<style name="AppTheme\.NoActionBarLaunch"[^>]*>)([\s\S]*?)(<\/style>)/;
if (!launch.test(xml)) throw new Error("AppTheme.NoActionBarLaunch not found in styles.xml");
writeFileSync(styles, xml.replace(launch, (_, open, _body, close) => `${open}
        <item name="android:background">@color/msplash_bg</item>
        <item name="windowSplashScreenBackground">@color/msplash_bg</item>
        <item name="windowSplashScreenAnimatedIcon">@drawable/msplash_blank</item>
    ${close}`));
console.log("quiet launch screen set");
