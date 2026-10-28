package com.funmburg.mingyue.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import com.funmburg.mingyue.R;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

// "Сегодня" 4×2: overdue and today's tasks, a tap on the circle marks one done
public class TodayWidget extends AppWidgetProvider {
    static final String TOGGLE = "com.funmburg.mingyue.widgets.TOGGLE";
    private static final int[] ROWS = {R.id.w_row0, R.id.w_row1, R.id.w_row2, R.id.w_row3};
    private static final int[] CHKS = {R.id.w_chk0, R.id.w_chk1, R.id.w_chk2, R.id.w_chk3};
    private static final int[] TITLES = {R.id.w_title0, R.id.w_title1, R.id.w_title2, R.id.w_title3};
    private static final int[] METAS = {R.id.w_meta0, R.id.w_meta1, R.id.w_meta2, R.id.w_meta3};

    @Override
    public void onUpdate(Context ctx, AppWidgetManager m, int[] ids) {
        render(ctx, m, ids);
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (TOGGLE.equals(intent.getAction())) {
            String id = intent.getStringExtra("id");
            boolean done = intent.getBooleanExtra("done", true);
            if (id != null) {
                WidgetStore.setDone(ctx, id, done);
                try {
                    WidgetStore.addAction(ctx, new JSONObject().put("type", "toggle").put("id", id).put("done", done));
                } catch (JSONException ignored) {}
                WidgetUi.refreshAll(ctx);
                MingyueWidgetsPlugin.pokePage();
            }
            return;
        }
        super.onReceive(ctx, intent);
    }

    static void render(Context ctx, AppWidgetManager m, int[] ids) {
        if (ids.length == 0) return;
        String t = WidgetStore.today();
        List<WidgetStore.Task> all = WidgetStore.todayTasks(ctx);
        int total = 0, done = 0;
        for (WidgetStore.Task k : all) {
            if (k.date.equals(t)) {
                total++;
                if (k.done) done++;
            }
        }
        for (int wid : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_today);
            v.setOnClickPendingIntent(R.id.w_root, WidgetUi.openApp(ctx));
            v.setTextViewText(R.id.w_day, WidgetUi.dayTitle());
            v.setTextViewText(R.id.w_count, total > 0 ? done + " из " + total : "");
            for (int i = 0; i < ROWS.length; i++) {
                if (i >= all.size()) {
                    v.setViewVisibility(ROWS[i], View.GONE);
                    continue;
                }
                WidgetStore.Task k = all.get(i);
                boolean late = !k.done && k.date.compareTo(t) < 0;
                v.setViewVisibility(ROWS[i], View.VISIBLE);
                v.setTextViewText(TITLES[i], k.title);
                v.setTextColor(TITLES[i], ctx.getColor(k.done ? R.color.mw_muted : R.color.mw_ink));
                v.setInt(TITLES[i], "setPaintFlags", k.done ? Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG : Paint.ANTI_ALIAS_FLAG);
                String meta = late ? "просрочено" : k.time;
                v.setTextViewText(METAS[i], meta);
                v.setViewVisibility(METAS[i], meta.isEmpty() || k.done ? View.GONE : View.VISIBLE);
                v.setTextColor(METAS[i], ctx.getColor(late ? R.color.mw_late : R.color.mw_soft));
                v.setImageViewResource(CHKS[i], k.done ? R.drawable.widget_chk_done : R.drawable.widget_chk);
                v.setContentDescription(CHKS[i], (k.done ? "Вернуть в работу: " : "Готово: ") + k.title);
                Intent tog = new Intent(ctx, TodayWidget.class)
                        .setAction(TOGGLE)
                        .setData(Uri.parse("mingyue://toggle/" + Uri.encode(k.id)))
                        .putExtra("id", k.id)
                        .putExtra("done", !k.done);
                v.setOnClickPendingIntent(CHKS[i], PendingIntent.getBroadcast(ctx, i, tog, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            }
            int more = all.size() - ROWS.length;
            v.setViewVisibility(R.id.w_more, more > 0 ? View.VISIBLE : View.GONE);
            v.setTextViewText(R.id.w_more, "ещё " + more);
            boolean empty = all.isEmpty();
            v.setViewVisibility(R.id.w_empty, empty ? View.VISIBLE : View.GONE);
            v.setTextViewText(R.id.w_empty, !WidgetStore.hasSnapshot(ctx) ? "Откройте Mingyue, чтобы задачи появились здесь" : "На сегодня задач нет");
            m.updateAppWidget(wid, v);
        }
    }
}
