package com.example.jessexing.solesoundglassudp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class AppService extends Service {
    private static final String TAG = "AppService";
    private static final String LIVE_CARD_ID = "HelloGlass";
    private static final long WAKE_LOCK_DURATION_IN_MILLIS = 3600000;

    // private AppDrawer mCallback;

    private LiveCard mLiveCard;

    @Override
    public void onCreate() {
    	Log.e(TAG, "onCreate");
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
    	Log.e(TAG, "onBind");
        return null;
    }

    private void createImmersionFor2DDrawing() {
        Intent intent = new Intent(this, Immersion2DActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this, app crashed - android.app.Service
        // is descendant of android.app.Context so you can use startActivity directly. However since you start
        // this outside any activity you need to set FLAG_ACTIVITY_NEW_TASK flag on the intent.
        startActivity(intent);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(TAG, "onStartCommand: true");


            createImmersionFor2DDrawing();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "OnDestroy()");

        super.onDestroy();
    }
}