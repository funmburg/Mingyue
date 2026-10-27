// Wraps the artifact source (app/mingyue.html, same file as the claude.ai artifact)
// in a full HTML document for the Android app: www/index.html.
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";

const body = readFileSync(new URL("../app/mingyue.html", import.meta.url), "utf8");
const head = `<!doctype html><html lang="ru"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover">
<meta name="theme-color" content="#FFFFFF">
<style>html,body{margin:0;padding:0}</style></head><body>
`;
mkdirSync(new URL("../www/", import.meta.url), { recursive: true });
writeFileSync(new URL("../www/index.html", import.meta.url), head + body + "\n</body></html>\n");
console.log("www/index.html written");
