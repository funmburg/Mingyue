package com.funmburg.mingyue.widgets;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// What the widgets show and what they did while the app was closed.
// The app's data lives in the WebView's localStorage, so the app pushes a snapshot here
// (snapshot) and picks up the widgets' changes from here (actions) the next time its page runs.
public final class WidgetStore {
    private static final String PREFS = "mingyue_widgets";
    private static final String SNAPSHOT = "snapshot";
    private static final String ACTIONS = "actions";

    private WidgetStore() {}

    public static final class Task {
        public final String id, title, date, time;
        public final boolean done, prio;

        Task(JSONObject o) {
            id = o.optString("id");
            title = o.optString("title");
            date = o.optString("date", "");
            time = o.optString("time", "");
            done = o.optBoolean("done");
            prio = o.optBoolean("prio");
        }
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized void setSnapshot(Context ctx, String json) {
        prefs(ctx).edit().putString(SNAPSHOT, json).apply();
    }

    static synchronized JSONObject snapshot(Context ctx) {
        try {
            return new JSONObject(prefs(ctx).getString(SNAPSHOT, "{}"));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static boolean hasSnapshot(Context ctx) {
        return prefs(ctx).contains(SNAPSHOT);
    }

    public static String name(Context ctx) {
        return snapshot(ctx).optString("name", "").trim();
    }

    public static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
    }

    // Overdue tasks first, then today's: open ones by importance and time, done ones last (same order as the app)
    public static List<Task> todayTasks(Context ctx) {
        String t = today();
        List<Task> out = new ArrayList<>();
        JSONArray arr = snapshot(ctx).optJSONArray("tasks");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o == null) continue;
                Task k = new Task(o);
                if (k.date.isEmpty()) continue;
                int c = k.date.compareTo(t);
                if (c == 0 || (c < 0 && !k.done)) out.add(k);
            }
        }
        Collections.sort(out, (a, b) -> {
            if (a.done != b.done) return a.done ? 1 : -1;
            boolean la = a.date.compareTo(t) < 0, lb = b.date.compareTo(t) < 0;
            if (la != lb) return la ? -1 : 1;
            if (a.prio != b.prio) return a.prio ? -1 : 1;
            String ta = a.time.isEmpty() ? "99" : a.time, tb = b.time.isEmpty() ? "99" : b.time;
            return ta.compareTo(tb);
        });
        return out;
    }

    // Marks the task in the snapshot right away, so the widget does not wait for the app
    static synchronized void setDone(Context ctx, String id, boolean done) {
        JSONObject s = snapshot(ctx);
        JSONArray arr = s.optJSONArray("tasks");
        if (arr == null) return;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o != null && id.equals(o.optString("id"))) {
                try {
                    o.put("done", done);
                } catch (JSONException ignored) {}
            }
        }
        setSnapshot(ctx, s.toString());
    }

    static synchronized void addAction(Context ctx, JSONObject action) {
        JSONArray arr;
        try {
            arr = new JSONArray(prefs(ctx).getString(ACTIONS, "[]"));
        } catch (JSONException e) {
            arr = new JSONArray();
        }
        arr.put(action);
        prefs(ctx).edit().putString(ACTIONS, arr.toString()).apply();
    }

    public static synchronized JSONArray takeActions(Context ctx) {
        JSONArray arr;
        try {
            arr = new JSONArray(prefs(ctx).getString(ACTIONS, "[]"));
        } catch (JSONException e) {
            arr = new JSONArray();
        }
        prefs(ctx).edit().remove(ACTIONS).apply();
        return arr;
    }
}
