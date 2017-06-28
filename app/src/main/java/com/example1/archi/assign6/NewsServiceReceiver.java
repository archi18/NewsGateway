package com.example1.archi.assign6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by archi on 5/4/2017.
 */

public class NewsServiceReceiver extends BroadcastReceiver implements DownloadCallback<String>{
    NewsService newsService;
    MainActivity mainActivity;
    DownloadTask downloadTask;
    boolean isDownloadActive =false;
    Context context;
    List<Artical> articalList;
    private static final String TAG = "NewsServiceReceiver";
    private static final String ARTICAL_JSON_FILE="ARTICAL";
    private static final String NO_RESP_REC="No response received.";
    public NewsServiceReceiver(NewsService newsService,MainActivity mainActivity) {
        this.newsService = newsService;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("NewsServiceReceiver ","onReceive" );
        switch (intent.getAction()) {
            case NewsService.ACTION_MSG_TO_SERVICE:
                if (intent.hasExtra("SOURCE_DATA")) {
                    String str =intent.getStringExtra("SOURCE_DATA");
                    downloadTask = new DownloadTask(this);
                    downloadTask.execute(MainActivity.getArticalLink(str));
                }
                break;
        }
    }

    /************************************DownloadCallback START***************************************************/

    @Override
    public void updateFromDownload(String result) {
        Log.d(TAG," Result => "+result);
        if (result == null || result.length() < 50 || result.equals(NO_RESP_REC)) {
            showCustomAlert("No Result", "Can not fetch data \n for location Entered.\n try Different Localtion");
        } else {
            Log.d(TAG,"Result --->" +result);
                readJsonArticalData(result);
                newsService.storyListUpdateFromDownload(articalList);
        }

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        final String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifiConn = networkInfo.isConnected();
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        return networkInfo;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishDownloading() {
        isDownloadActive = false;
    }
    /*********************************************DownloadCallback END************************************************/
    /***********************************JSON DATA Reading Start*************************************************/

    private List<Artical> readJsonArticalData(String jsonData){
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JasonFileHandler jasonFileHandler= new JasonFileHandler(context);
            articalList =  jasonFileHandler.readArticalData(inputStream,ARTICAL_JSON_FILE);
            Log.d(TAG," List "+articalList.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return articalList;
    }
    /***********************************JSON DATA Reading END*************************************************/
    /*********************************************CUSTOM ALERT START*********************************************/

    public void showCustomAlert(String alertTitle, String alertMessage){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.activity_dialog_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        final TextView tittle = (TextView) promptView.findViewById(R.id.alertTitile);
        final TextView message = (TextView) promptView.findViewById(R.id.alertMsg);
        tittle.setText(alertTitle);
        message.setText(alertMessage);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    /*******************************************CUSTOM ALERT END*********************************************/

}

