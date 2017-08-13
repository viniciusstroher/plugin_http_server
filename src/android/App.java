package org.apache.cordova.httpd;
    
import java.io.IOException;
import java.util.Map;
import android.util.Log;
import java.util.Arrays;
import fi.iki.elonen.NanoHTTPD;
// NOTE: If you're using NanoHTTPD < 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {
    private String senha;
    public App(int p, String senha)  {
        super(p);
        senha = senha;
    }

    @Override
    public Response serve(IHTTPSession session) {
        //session.set
        Method method = session.getMethod();
        String hookReturn = "{\"api\":\"no hooks\"}";
        Map<String, String> headers = session.getHeaders();
        Map<String, String> params  = session.getParms();
        Log.i(Httpd.LOG_TAG,"Recebendo Request");
        Log.i(Httpd.LOG_TAG,Arrays.asList(headers)); 
        Log.i(Httpd.LOG_TAG,Arrays.asList(params)); 

        // se nao for post, n aceita o request
        if (!Method.POST.equals(method)) {
            hookReturn="{\"api\":\"use post method\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(headers.get("Api-Key") == null){
            hookReturn="{\"api\":\"Api-Key not exists in header\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(!headers.get("Api-Key").equals(senha)){
            hookReturn="{\"api\":\"Api-Key wrong\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        
        Httpd.pluginWebView.loadUrl("javascript:!Array.isArray(window.httpd.requests[\""+session.getUri()+"\"]) ? window.httpd.requests[\""+session.getUri()+"\"] = [] : null ;");                    
        Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests[\""+session.getUri()+"\"].push({retorno:1}) ;");                    
          
        hookReturn = "{\"api\":\"ok\"}";
        return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        
    }
}