package com.example1.archi.assign6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by archi on 5/4/2017.
 */

public class NewsReceiver extends BroadcastReceiver {
    MainActivity mainActivity;
    List<Artical> articalStoryList;
    public static final String TAG ="NewsReceiver";
    public NewsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case MainActivity.ACTION_NEWS_STORY:
                Log.d(TAG,"artical list received ");
                if (intent.hasExtra(MainActivity.SERILIZE_STORY_LIST)) {
                    this.articalStoryList = (List<Artical>) intent.getSerializableExtra(MainActivity.SERILIZE_STORY_LIST);
                    mainActivity.reDoFragments(this.articalStoryList);
                    Log.d("NewsReceiver ",Integer.toString(mainActivity.articalList.size()));
                }
                break;
        }
    }
}
