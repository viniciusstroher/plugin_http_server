package org.apache.cordova.httpd;
    
import java.io.IOException;
import java.util.Map;
import android.util.Log;
//import org.nanohttpd.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD;
// NOTE: If you're using NanoHTTPD < 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {

    public App()  {
        
        super(8090);
        Log.i(Httpd.LOG_TAG,"Iniciando servidor na porta:8090");
        try{
            //start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }catch(IOException e){
            Log.i(Httpd.LOG_TAG,"Error start server: "+e.getMessage());
        }
        Log.i(Httpd.LOG_TAG,"\nRunning! Point your browsers to http://localhost:8090/");
        
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}