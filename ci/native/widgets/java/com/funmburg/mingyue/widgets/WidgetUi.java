package com.funmburg.mingyue.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

final class WidgetUi {
    private WidgetUi() {}

    static void refreshAll(Context ctx) {
        AppWidgetManager m = AppWidgetManager.getInstance(ctx);
        TodayWidget.render(ctx, m, m.getAppWidgetIds(new ComponentName(ctx, TodayWidget.class)));
        MigoWidget.render(ctx, m, m.getAppWidgetIds(new ComponentName(ctx, MigoWidget.class)));
    }

    static PendingIntent openApp(Context ctx) {
        Intent i = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        if (i == null) i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static String plural(int n, String one, String few, String many) {
        int m10 = n % 10, m100 = n % 100;
        if (m10 == 1 && m100 != 11) return one;
        if (m10 >= 2 && m10 <= 4 && (m100 < 12 || m100 > 14)) return few;
        return many;
    }

    static String greeting() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h >= 5 && h < 12) return "Доброе утро";
        if (h >= 12 && h < 17) return "Добрый день";
        if (h >= 17 && h < 23) return "Добрый вечер";
        return "Доброй ночи";
    }

    static final String[] DOW = {"Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};

    static String dayTitle() {
        Calendar c = Calendar.getInstance();
        return DOW[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + c.get(Calendar.DAY_OF_MONTH);
    }
}
