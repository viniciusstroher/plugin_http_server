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
            int porta       = Integer.parseInt(intent.getExtras().get("PORTA").toString());
            String senha    = intent.getExtras().get("SENHA").toString();
            Log.i(Httpd.LOG_TAG,"Iniciando servidor na porta: "+porta+" Senha: "+senha);

            server          = new App(porta,senha);
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
        return null;
    }
 
    private class SyncDataTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... p) {
            return null;
        }
    }
}