// Adds the home-screen widgets to the generated Android project (android/ is not committed):
// copies ci/native/widgets/{java,res} into android/app/src/main and registers them in the manifest.
import { cpSync, readFileSync, writeFileSync } from "node:fs";

const src = new URL("../ci/native/widgets/", import.meta.url);
const main = new URL("../android/app/src/main/", import.meta.url);
cpSync(new URL("java/", src), new URL("java/", main), { recursive: true });
cpSync(new URL("res/", src), new URL("res/", main), { recursive: true });

const manifestUrl = new URL("AndroidManifest.xml", main);
const manifest = readFileSync(manifestUrl, "utf8");
if (!manifest.includes(".widgets.TodayWidget")) {
  const block = readFileSync(new URL("manifest.xml", src), "utf8");
  if (!manifest.includes("</application>")) throw new Error("AndroidManifest.xml has no </application>");
  writeFileSync(manifestUrl, manifest.replace("</application>", block + "    </application>"));
}
console.log("widgets added to android/");
