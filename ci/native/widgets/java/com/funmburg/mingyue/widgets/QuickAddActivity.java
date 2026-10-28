package com.funmburg.mingyue.widgets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import com.funmburg.mingyue.R;
import org.json.JSONException;
import org.json.JSONObject;

// Input over the home screen. The text goes to the app as is, so its own parser
// understands "завтра", "пт", "18:00" and "!" exactly as in the app.
public class QuickAddActivity extends Activity {
    private EditText input;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.widget_quickadd_dialog);
        input = findViewById(R.id.w_input);
        input.setOnEditorActionListener((v, id, ev) -> {
            boolean enter = ev != null && ev.getKeyCode() == KeyEvent.KEYCODE_ENTER && ev.getAction() == KeyEvent.ACTION_DOWN;
            if (id == EditorInfo.IME_ACTION_DONE || enter) {
                submit();
                return true;
            }
            return false;
        });
        findViewById(R.id.w_send).setOnClickListener(v -> submit());
    }

    private void submit() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) {
            finish();
            return;
        }
        try {
            WidgetStore.addAction(this, new JSONObject().put("type", "add").put("text", text));
        } catch (JSONException ignored) {}
        if (MingyueWidgetsPlugin.pokePage()) {
            Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show();
        } else {
            // the app is closed: open it, it adds the task while starting
            Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (i != null) startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        finish();
    }
}
