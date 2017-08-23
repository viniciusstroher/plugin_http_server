package org.apache.cordova.httpd;
import android.content.Context;    
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Vibrator;
// NOTE: If you're using NanoHTTPD < 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import fi.iki.elonen.NanoHTTPD;
import android.app.AlarmManager;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.content.pm.PackageManager;



public class App extends NanoHTTPD {
    private String senha;
    public  static ArrayList<JSONObject>  fileRequestsEsperando = new ArrayList<JSONObject>();
    public App(int p, String senha)  {
        super(p);
        this.senha = senha;
    }

    @Override
    public Response serve(IHTTPSession session) {
        //session.set
        Method method = session.getMethod();
        String hookReturn = "{\"api\":\"no hooks\"}";
        Map<String, String> headers = session.getHeaders();
        Log.i(Httpd.LOG_TAG,"Recebendo Request");
        
        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject json;
        try {
            //USAR RAW - application/json no postrman
            session.parseBody(map);
            
            json = new JSONObject(map.get("postData"));
            Log.i(Httpd.LOG_TAG,"Params : "+ json);  
            
            //ADD DATE TO JSON
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String agora                = dateFormat.format(new Date());
            json.put("date", agora); 
            //add uri
            json.put("uri", session.getUri()); 

        } catch (Exception e) {
            json = new JSONObject();
            Log.i(Httpd.LOG_TAG,"Error parseBody : "+e.getMessage());  
            hookReturn = "Body parser error";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }



        String key    = "";
        String value  = "";
        for (Map.Entry<String,String> entry : headers.entrySet()) {
          key   = entry.getKey();
          value = entry.getValue();
          if(key.equals("http-client-ip")){
            try {
                json.put("ip", value); 
            }catch(Exception e){
                Log.i(Httpd.LOG_TAG,"Sem ip : "+e.getMessage());  
            }

          }

          if(key.equals("agente")){
            try {
                json.put("agente", value); 
            }catch(Exception e){
                Log.i(Httpd.LOG_TAG,"Sem agente : "+e.getMessage());  
            }
            
          }
          Log.i(Httpd.LOG_TAG,"Headers : "+ key + " - " + value);  
        }

        
        // se nao for post, n aceita o request
        if (!Method.POST.equals(method)) {
            hookReturn="{\"api\":\"use post method\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(headers.get("api-key") == null){
            hookReturn="{\"api\":\"api-key not exists in header\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        Log.i(Httpd.LOG_TAG,"Autorizacao : "+ this.senha + " - " + headers.get("api-key"));  
        if(!headers.get("api-key").equals(this.senha)){
            hookReturn="{\"api\":\"api-key wrong\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(!Httpd.background){
            Httpd.pluginWebView.loadUrl("javascript:!Array.isArray(window.httpd.requests[\""+session.getUri()+"\"]) ? window.httpd.requests[\""+session.getUri()+"\"] = [] : null ;");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests[\""+session.getUri()+"\"].push("+json.toString()+") ;");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd[\"contador\"]+=1;");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd[\"ultimaUri\"]=\""+session.getUri()+"\";");                    
        }else{
            //se estiver em background coloca em uma pilha e espera o resume
            App.fileRequestsEsperando.add(json);

            //ADICIONAR NOTIFICAÇÃO LOCAL PARA QUANDO PASSAR TANTOS REQUESTS 1 a N SE 
            //ESTIVER EM BACKGROUND
            //VIBRAR QUANDO GANHA REQUEST EM BACKGROUND
            Vibrator v = (Vibrator) Httpd.pluginContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
            
            Intent i = new Intent(getActivity()); 

            Intent notificationIntent = pm.getLaunchIntentForPackage("com.racionaltec");

            NotificationCompat.Builder b = new NotificationCompat.Builder(Httpd.pluginContext);
            PendingIntent contentIntent = PendingIntent.getActivity(Httpd.pluginContext, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            b.setAutoCancel(true)
           .setDefaults(Notification.DEFAULT_ALL)
           .setWhen(System.currentTimeMillis())         
           .setSmallIcon(Httpd.pluginContext.getApplicationInfo().icon)
           .setTicker("Evento")            
           .setContentTitle("Evento recebido!!")
           .setContentText("Voce recebeu um novo evento, confira já.")
           .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
           .setContentIntent(contentIntent)
           .setContentInfo("Info");
            
            NotificationManager notificationManager = (NotificationManager) Httpd.pluginContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());

        }
        hookReturn = "{\"api\":\"ok\"}";
        return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        
    }

     
}