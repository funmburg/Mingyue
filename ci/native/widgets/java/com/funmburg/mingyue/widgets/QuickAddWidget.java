package com.funmburg.mingyue.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.funmburg.mingyue.R;

// "Быстро добавить" 4×1: opens a small input over the home screen
public class QuickAddWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context ctx, AppWidgetManager m, int[] ids) {
        Intent i = new Intent(ctx, QuickAddActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        for (int wid : ids) {
            RemoteViews v = new RemoteViews(ctx.getPackageName(), R.layout.widget_quickadd);
            v.setOnClickPendingIntent(R.id.w_root, pi);
            m.updateAppWidget(wid, v);
        }
    }
}
