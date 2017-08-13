package org.apache.cordova.httpd;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;

public class Httpd extends CordovaPlugin {
    public static final String LOG_TAG = "Httpd";
    public static CordovaWebView pluginWebView;
    private CallbackContext callbackContext;
    private JSONObject params;

    /**
     * Remember last device orientation to detect orientation changes.
     */
    private int orientation;
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Httpd.pluginWebView = webView; 

    }
    // Helper to be compile-time compatible with both Cordova 3.x and 4.x.
    private View getView() {
        try {
            return (View)webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            return (View)webView;
        }
    }

    @Override
    protected void pluginInitialize() {
        Httpd.pluginWebView = webView; 
    }


    @Override
    public void onPause(boolean multitasking) {

    }

    @Override
    public void onDestroy() {

    }


    //COMANDO EXECUTE
    //AQUI FICAO AS ACOES
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(action.equals("startHttpd")){
            Context context=this.cordova.getActivity().getApplicationContext(); 
            Intent i = new Intent(context, HttpdService.class);

            JSONObject params = args.getJSONObject(0);
            int porta         = params.getInt("porta");
            i.putExtra("PORTA",porta);
            
            Log.i(LOG_TAG,"Iniciando serviço na porta:"+porta);
            //CRIA requisiçao dos requests para o appjs
            Httpd.pluginWebView.loadUrl("javascript:window.httpd = {};");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests = {};");                    
          
            context.startService(i);
        }
        
        callbackContext.success();
        return true;
    }


    //ENVIO DE MENSAGEMS A CLASSE
    @Override
    public Object onMessage(String id, Object data) {
        return null;
    }

    // Don't add @Override so that plugin still compiles on 3.x.x for a while
    public void onConfigurationChanged(Configuration newConfig) {
       
    }

}
