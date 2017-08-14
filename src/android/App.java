package org.apache.cordova.httpd;
    
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
            Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

        }
        hookReturn = "{\"api\":\"ok\"}";
        return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        
    }

     
}