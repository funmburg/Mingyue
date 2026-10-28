package com.funmburg.mingyue.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.funmburg.mingyue.R;

// "Миго" 2×2: the mascot, a greeting for the time of day and how many tasks are left today
public class MigoWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager m, int[] ids) {
        render(ctx, m, ids);
    }

    static void render(Context ctx, AppWidgetManager m, int[] ids) {
        if (ids.length == 0) return;
        String t = WidgetStore.today();
        int total = 0, left = 0;
        for (WidgetStore.Task k : WidgetStore.todayTasks(ctx)) {
            if (k.date.equals(t)) total++;
            if (!k.done) left++;
        }
        String name = WidgetStore.name(ctx);
        String hello = WidgetUi.greeting() + (name.isEmpty() ? "" : ", " + name);
        String sub = left > 0 ? "осталось " + left + " " + WidgetUi.plural(left, "дело", "дела", "дел")
                : total > 0 ? "всё сделано" : "дел на сегодня нет";
        for (int wid : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_migo);
            v.setOnClickPendingIntent(R.id.w_root, WidgetUi.openApp(ctx));
            v.setTextViewText(R.id.w_hello, hello);
            v.setTextViewText(R.id.w_sub, sub);
            m.updateAppWidget(wid, v);
        }
    }
}
