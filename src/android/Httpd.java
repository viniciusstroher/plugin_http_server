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
    public static Context pluginContext;
    public static boolean background;
    public static App app;
    public static CordovaInterface ci;
    private CallbackContext callbackContext;
    private JSONObject params;

    /**
     * Remember last device orientation to detect orientation changes.
     */
    private int orientation;
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Httpd.app           = null;
        Httpd.pluginWebView = webView; 
        Httpd.pluginContext = this.cordova.getActivity().getApplicationContext();
        Httpd.ci            = cordova;
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
        Httpd.app            = null;
        Httpd.pluginWebView = webView;
        Httpd.background    = false; 
        Httpd.pluginWebView.loadUrl("javascript:window.httpd = {contador:0,ultimaUri:\"\",requests: {}};");                    
          
    }

    @Override
    public void onResume(boolean multitasking) {
        Httpd.background = false;
        //atualiza requests no resume

        if(App.fileRequestsEsperando.size() > 0){
            Log.i(LOG_TAG,"Atualizando requests no app JSONS: "+App.fileRequestsEsperando.size());
            for(int i=0;i<App.fileRequestsEsperando.size();i++){
                
                
                JSONObject aux = App.fileRequestsEsperando.get(i);
                try{
                    Httpd.pluginWebView.loadUrl("javascript:!Array.isArray(window.httpd.requests[\""+aux.get("uri")+"\"]) ? window.httpd.requests[\""+aux.get("uri")+"\"] = [] : null ;");                    
                    Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests[\""+aux.get("uri")+"\"].push("+aux.toString()+") ;");                    
                    Httpd.pluginWebView.loadUrl("javascript:window.httpd[\"contador\"]+=1;");                    
                    Httpd.pluginWebView.loadUrl("javascript:window.httpd[\"ultimaUri\"]=\""+aux.get("uri")+"\";");                    
                }catch(Exception e){
                    Log.i(LOG_TAG,"Error get JSONObject: "+e.getMessage());
                }

                App.fileRequestsEsperando.remove(i);
            }
        }
       

    }

    @Override
    public void onPause(boolean multitasking) {
        Httpd.background = true;
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
            String senha      = params.getString("senha");
            i.putExtra("PORTA",porta);
            i.putExtra("SENHA",senha);
            
            Log.i(LOG_TAG,"Iniciando serviço na porta:"+porta+" Senha: "+senha);
            //CRIA requisiçao dos requests para o appjs
            Httpd.pluginWebView.loadUrl("javascript:window.httpd = {contador:0,ultimaUri:\"\",requests: {}};");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd_sever=true;");                    
          
            context.startService(i);
        }

        if(action.equals("stopHttpd")){
            if(Httpd.app != null){
                try{
                    Httpd.app.stop();
                }catch(Exception e){

                }
                Httpd.pluginWebView.loadUrl("javascript:window.httpd_sever=false;"); 
                Httpd.app            = null;
            }
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
