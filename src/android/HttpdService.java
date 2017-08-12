package org.apache.cordova.httpd;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.lang.Integer;
import fi.iki.elonen.NanoHTTPD;
public class HttpdService extends Service {
    public App server;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            String porta = intent.getStringExtra("PORTA");
            int    portaI= Integer.parseInt(porta);
            server       = new App();
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

            return START_NOT_STICKY;
        }catch(Exception e){
            Log.i(Httpd.LOG_TAG,"Error onStartCommand: "+e.getMessage());
        }
        return START_NOT_STICKY;
    }
    
    @Override
    public void onDestroy(){

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Neste exemplo, iremos supor que o service será invocado apenas
        // atraves de startService()
        return null;
    }
 
    private class SyncDataTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... p) {
            //DataSync d = new DataSync(HttpdService.this);
            //d.syncPendingNotes();
            return null;
        }
    }
}