// Wraps the artifact source (app/mingyue.html, same file as the claude.ai artifact)
// in a full HTML document for the Android app: www/index.html.
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";

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
body::before{content:"";position:fixed;z-index:45;top:0;left:0;right:0;height:env(safe-area-inset-top,0px);background:var(--h-bg,#fff);pointer-events:none}
</style></head><body>
`;
mkdirSync(new URL("../www/", import.meta.url), { recursive: true });
writeFileSync(new URL("../www/index.html", import.meta.url), head + body + "\n</body></html>\n");
console.log("www/index.html written");
