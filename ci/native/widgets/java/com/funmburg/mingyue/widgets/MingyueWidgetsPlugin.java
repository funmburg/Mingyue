package com.funmburg.mingyue.widgets;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// Bridge between the app page and the home-screen widgets (registered in MainActivity).
@CapacitorPlugin(name = "MingyueWidgets")
public class MingyueWidgetsPlugin extends Plugin {
    private static MingyueWidgetsPlugin live;

    @Override
    public void load() {
        live = this;
    }

    @Override
    protected void handleOnDestroy() {
        if (live == this) live = null;
    }

    // The app page calls this after every save: {data: JSON string of {name, tasks}}
    @PluginMethod
    public void update(PluginCall call) {
        String data = call.getString("data");
        if (data != null) {
            WidgetStore.setSnapshot(getContext(), data);
            WidgetUi.refreshAll(getContext());
        }
        call.resolve();
    }

    // Changes made on the widgets that the page has not applied yet
    @PluginMethod
    public void takeActions(PluginCall call) {
        JSObject r = new JSObject();
        r.put("actions", WidgetStore.takeActions(getContext()));
        call.resolve(r);
    }

    // Tells a running page to pick up new actions; false when the app's page is not running
    static boolean pokePage() {
        MingyueWidgetsPlugin p = live;
        if (p == null || p.getBridge() == null) return false;
        p.notifyListeners("actions", new JSObject());
        return true;
    }
}
