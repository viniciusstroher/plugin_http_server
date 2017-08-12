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

    public App(int p)  {
        super(p);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //session.set

        if (!session.getUri().equalsIgnoreCase("/")){
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/json", "{api:true}");
        }

        /*String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse(msg + "</body></html>\n");

        if (!session.getUri().equalsIgnoreCase("/info"))
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Resource not found.");

        if (!session.getMethod().equals(Method.GET))
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Method not allowed.");

        String response = "{\"working\":{}}";
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response);
        */
    }
}