// Wraps the artifact source (app/mingyue.html, same file as the claude.ai artifact)
// in a full HTML document for the Android app: www/index.html.
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";

const build = process.env.GITHUB_SHA ? `${process.env.GITHUB_RUN_NUMBER || ""} · ${process.env.GITHUB_SHA.slice(0, 7)}` : "local";
const body = readFileSync(new URL("../app/mingyue.html", import.meta.url), "utf8");
const head = `<!doctype html><html lang="ru"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">
<meta name="theme-color" content="#FFFFFF">
<style>
/* Android draws the app edge to edge: keep content out of the status bar and the gesture bar,
   as the claude.ai host page does for the artifact */
:root{box-sizing:border-box;padding-top:env(safe-area-inset-top,0px);padding-bottom:env(safe-area-inset-bottom,0px)}
html{scroll-padding-top:env(safe-area-inset-top,0px)}
html,body{margin:0;padding:0}
html{-webkit-text-size-adjust:100%;text-size-adjust:100%}
body::before{content:"";position:fixed;z-index:45;top:0;left:0;right:0;height:env(safe-area-inset-top,0px);background:var(--h-bg,#fff);pointer-events:none}
</style>
<script>
window.MINGYUE_BUILD = ${JSON.stringify(build)};
// Android's system font size would scale the whole layout up and break it; the app keeps its own sizes
window.Capacitor?.Plugins?.TextZoom?.set({ value: 1 }).catch(() => {});
</script></head><body>
`;
mkdirSync(new URL("../www/", import.meta.url), { recursive: true });
writeFileSync(new URL("../www/index.html", import.meta.url), head + body + "\n</body></html>\n");
console.log("www/index.html written");
