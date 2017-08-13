package org.apache.cordova.httpd;
    
import java.io.IOException;
import java.util.Map;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
// NOTE: If you're using NanoHTTPD < 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {

    public App(int p)  {
        super(p);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //session.set
        Method method = session.getMethod();
        String hookReturn = "{api:'no hooks'}";

        // se nao for post, n aceita o request
        if (!Method.POST.equals(method)) {
            hookReturn="{api:'use post method'}";
            return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        }

        if (!session.getUri().equalsIgnoreCase("/")){
            hookReturn="{api:'working'}";
        }else{
            //criar jsonobject com oque vier e voltar para o app js
            //usar callback do execute do cordova
            Httpd.pluginWebView.loadUrl("javascript:!Array.isArray(window.httpd.requests[\""+session.getUri()+"\"]) ? window.httpd.requests[\""+session.getUri()+"\"] = [] : null ;");                    
            Httpd.pluginWebView.loadUrl("javascript:window.httpd.requests[\""+session.getUri()+"\"].push({retorno:1}) ;");                    
          
        }
        return newFixedLengthResponse(Response.Status.OK, "text/json", hookReturn);
        
    }
}