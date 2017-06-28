package com.example1.archi.assign6;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris main brain behind master logic on 5/4/2017.
 */

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private boolean running = true;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    List<Artical> storyList = new ArrayList<>();
    NewsServiceReceiver newsServiceReceiver;
    IntentFilter msgToServiceIntentFilter;
    MainActivity mainActivity=null;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        newsServiceReceiver = new NewsServiceReceiver(this,mainActivity);
        msgToServiceIntentFilter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(newsServiceReceiver,msgToServiceIntentFilter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!running) {
                    Log.d(TAG, "run: Thread loop stopped early");
                }
                try {
                    while(true){
                        if(storyList.size()==0){
                            Thread.sleep(250);
                        }else{
                            broadcastStoryListUpdate();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }

    private void broadcastStoryListUpdate(){
        Log.d(TAG," Boradcasting list update "+storyList.size());
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.SERILIZE_STORY_LIST, (Serializable) storyList);
        sendBroadcast(intent);
        storyList.clear();
    }

    public void storyListUpdateFromDownload(List<Artical> newStoryList){
        this.storyList.clear();
        Log.d(TAG," update from download story list");
        this.storyList = newStoryList;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(newsServiceReceiver);
        running = false;
/*
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
*/
    }


}
