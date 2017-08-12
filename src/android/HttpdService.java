package org.apache.cordova.httpd;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import  android.util.Log;
public class HttpdService extends Service {
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DataSync d = new DataSync(HttpdService.this);
        d.syncPendingNotes();
        return START_NOT_STICKY;
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        // Neste exemplo, iremos supor que o service será invocado apenas
        // atraves de startService()
        return null;
    }
 
    private class SyncDataTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... p) {
            DataSync d = new DataSync(HttpdService.this);
            d.syncPendingNotes();
            return null;
        }
    }
}