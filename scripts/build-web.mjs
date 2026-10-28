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
// Home-screen widgets (APK only). Runs after the app's script, so it uses the app's own globals:
// after every save it sends the widgets the tasks they show, and it applies what was done
// on the widgets (a task marked done, a quick-added task) with the app's own functions.
const widgets = `<script>
(() => {
  const W = window.Capacitor?.Plugins?.MingyueWidgets;
  if (!W) return;
  let timer;
  const push = () => {
    clearTimeout(timer);
    timer = setTimeout(() => {
      const td = today(), until = ymd(addDays(new Date(), 1));
      const list = tasks().filter(x => x.date && x.date <= until && (!x.done || x.date >= td))
        .map(x => ({ id: x.id, title: x.title, date: x.date, time: x.time || "", done: !!x.done, prio: !!x.prio }));
      W.update({ data: JSON.stringify({ name: myName(), tasks: list }) }).catch(() => {});
    }, 300);
  };
  const setItem = Storage.prototype.setItem;
  Storage.prototype.setItem = function (k, v) {
    setItem.call(this, k, v);
    if (this === localStorage && (k === LS || k === PREFS_KEY)) push();
  };
  const apply = async () => {
    if (!ready) return void setTimeout(apply, 400);
    const { actions = [] } = await W.takeActions().catch(() => ({}));
    for (const a of actions) {
      const t = a.type === "toggle" && S.tasks[a.id];
      if (t && !!t.done !== a.done) toggleTask(t);
      if (a.type === "add" && a.text && addTask(a.text, today())) toast("Добавлено: " + a.text);
    }
    push();
  };
  W.addListener("actions", apply);
  document.addEventListener("visibilitychange", () => { if (!document.hidden) apply(); });
  apply();
})();
</script>`;
writeFileSync(new URL("../www/index.html", import.meta.url), head + body + "\n" + widgets + "\n</body></html>\n");
console.log("www/index.html written");
