package org.apache.cordova.httpd;
    
import java.io.IOException;
import java.util.Map;
import android.util.Log;
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
        Log.i(Httpd.LOG_TAG,"Recebendo Request");
        String key    = "";
        String value  = "";


        HashMap<String, String> map = new HashMap<String, String>();
        try {
            session.parseBody(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String json = map.get("postData");
        
        for (Map.Entry<String,String> entry : headers.entrySet()) {
          key   = entry.getKey();
          value = entry.getValue();
          Log.i(Httpd.LOG_TAG,"Headers : "+ key + " - " + value);  

          // do stuff
        }

        Log.i(Httpd.LOG_TAG,"Params : "+ json);  

        // se nao for post, n aceita o request
        if (!Method.POST.equals(method)) {
            hookReturn="{\"api\":\"use post method\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(headers.get("api-key") == null){
            hookReturn="{\"api\":\"api-key not exists in header\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if(!headers.get("api-key").equals(senha)){
            hookReturn="{\"api\":\"api-key wrong\"}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        
        Httpd.pluginWebView.loadUrl("javascript:!Array.isArray(window.httpd.requests[\""+session.getUri()+"\"]) ? window.httpd.requests[\""+session.getUri()+"\"] = [] : null ;");                    
        Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests[\""+session.getUri()+"\"].push("+json+") ;");                    
          
        hookReturn = "{\"api\":\"ok\"}";
        return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        
    }
}