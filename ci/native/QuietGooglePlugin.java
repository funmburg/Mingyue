package com.funmburg.mingyue;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.Scope;
import java.util.ArrayList;
import java.util.List;

// A fresh Google access token without any sign-in sheet: works once the user has
// granted the scopes in a real sign-in; otherwise rejects with "needs_ui".
@CapacitorPlugin(name = "QuietGoogle")
public class QuietGooglePlugin extends Plugin {
    @PluginMethod
    public void token(PluginCall call) {
        List<Scope> scopes = new ArrayList<>();
        try {
            JSArray arr = call.getArray("scopes");
            for (int i = 0; arr != null && i < arr.length(); i++) scopes.add(new Scope(arr.getString(i)));
        } catch (Exception e) {
            call.reject("bad scopes");
            return;
        }
        if (scopes.isEmpty()) {
            call.reject("no scopes");
            return;
        }
        AuthorizationRequest request = AuthorizationRequest.builder().setRequestedScopes(scopes).build();
        Identity.getAuthorizationClient(getContext())
            .authorize(request)
            .addOnSuccessListener(result -> {
                String token = result.getAccessToken();
                if (result.hasResolution() || token == null) call.reject("needs_ui");
                else call.resolve(new JSObject().put("accessToken", token));
            })
            .addOnFailureListener(e -> call.reject(e.getMessage() == null ? "failed" : e.getMessage()));
    }
}
