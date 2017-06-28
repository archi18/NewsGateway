package com.example1.archi.assign6;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements DownloadCallback<String>{



    /**********UI Fragment Variable  *****************/
    private PageAdapter myPageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    ImageView imageView;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    static int source_pos;
    /**********Class  Variable *****************/
    DownloadTask downloadTask;
    List<Source> sourceList;
    List<Artical> articalList;
    String downLoadAction;
    Set<String> categorySet=null;
    List<String> sourceNameList;
    List<Fragment> fragmentList;
    boolean isDownloadActive =false;
    NewsReceiver newsReceiver;
    Intent intent;
    Map<String,Source> sourceNameMap;
    InstanceState instanceState;
    /**********Final Static Variable *****************/
    private static final String TAG = "MainActivity";
    private static final String NO_RESP_REC="No response received.";
    private static final String SOURCE_JSON_FILE="SOURCE";
    private static final String ARTICAL_JSON_FILE="ARTICAL";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String SERILIZE_STORY_LIST = "ACTION_MSG_TO_SERVICE";
    static final String ARTICLES_DATA = "ARTICLES_DATA";
    static final String RESTORE_SERILIZE_STORYLIST = "RESTORE_SERILIZE_STORYLIST";
    static final String RESTORE_CATEGORY = "RESTORE_CATEGORY";
    private static final String CATEGORY_ALL = "ALL";
    private static final String INSTANCE_STATE = "INSTANCE_STATE";
    /***********************************************/

    static String sourcename;
    static String lastCatSelected=CATEGORY_ALL;
    static String lastSourceName="News Home";
    static int lastfragmentBaseID=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downLoadAction=null;
        setTitle(lastSourceName);
        newsReceiver = new NewsReceiver(this);
        intent = new Intent(MainActivity.this,NewsService.class);
        Log.d(TAG, "startService Called");
        startService(intent);
        IntentFilter actionNwsStryFilter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver,actionNwsStryFilter);
        sourceNameList = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.imageView);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_item, sourceNameList));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getNewsArticalForSource(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragmentList();
        myPageAdapter = new PageAdapter(getSupportFragmentManager());
        myPageAdapter.baseId=lastfragmentBaseID;
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(myPageAdapter);
        //pager.setBackground();
        if(isOnline()) {
            if (lastCatSelected.equalsIgnoreCase(CATEGORY_ALL))
                startDownload(getAllSourceAPILink(), SOURCE_JSON_FILE);
            else {
                startDownload(getCategoryLink(lastCatSelected), SOURCE_JSON_FILE);
            }
        }else{
            showCustomAlert("Not online.","Kindly check internet connectivity");
        }
    }
    /***********************************************************************************************************************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        instanceState = new InstanceState();
        instanceState.setArticalList(articalList);
        instanceState.setCategorySet(categorySet);
        instanceState.setLastCatSelected(lastCatSelected);
        instanceState.setLastSourceName(lastSourceName);
        instanceState.setLastfragmentBase(lastfragmentBaseID);
        outState.putSerializable(INSTANCE_STATE,instanceState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        instanceState =(InstanceState) savedInstanceState.getSerializable(INSTANCE_STATE);
        List<Artical> restoreArtialList = instanceState.getArticalList();
        categorySet = instanceState.getCategorySet();
        lastCatSelected = instanceState.getLastCatSelected();
        lastSourceName = instanceState.getLastSourceName();
        setTitle(lastSourceName);
        lastfragmentBaseID = instanceState.getLastfragmentBase();
        reDoFragments(restoreArtialList);
        super.onRestoreInstanceState(savedInstanceState);
    }
     /**********************************************************************************************************************/
    /********************************************Changes for FRAGMENT START****************************************************************/

    private void getNewsArticalForSource(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pager.setBackground(null);
        }
        Toast.makeText(this, sourceNameList.get(position), Toast.LENGTH_SHORT).show();
        source_pos=position;
        Log.d("source_pos ",sourceNameList.get(position));
        lastSourceName=sourceNameList.get(position);
        setTitle(sourceNameList.get(position));
        Intent intentz = new Intent();
        intentz.setAction(MainActivity.ACTION_MSG_TO_SERVICE);
        String sourceName = sourceNameList.get(position);
        String sourceID = sourceNameMap.get(sourceName).getSourceID();
        intentz.putExtra("SOURCE_DATA",sourceID);
        sendBroadcast(intentz);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void createDrawerListView(){
        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_item, sourceNameList));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getNewsArticalForSource(position);
                    }
                }
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragmentList();
        myPageAdapter = new PageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(myPageAdapter);
    }

    public void reDoFragments(List<Artical> newStoryArticalList){
        if(newStoryArticalList ==null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pager.setBackground(null);
        }
        for (int i = 0; i < myPageAdapter.getCount(); i++)
            myPageAdapter.notifyChangeInPosition(i);
        int articalCount=1;
        if(articalList !=null){
            articalList.clear();
        }
        articalList = newStoryArticalList;
        fragments.clear();
        Log.d("Fragment count xxxxx",Integer.toString(fragments.size()));
        Log.d("reDoFragments ", Integer.toString(articalList.size()));
        Iterator<Artical> articalIterator = newStoryArticalList.iterator();
        while (articalIterator.hasNext()){
            fragments.add(NewsFragment.newInstance(articalIterator.next(),articalCount+" of "+newStoryArticalList.size(),this));
            articalCount++;
        }
        Log.d("Fragment contttttttt ",Integer.toString(fragments.size()));
        myPageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

    }
    /***********************************************Changes for FRAGMENT END**************************************************************/

    /************************************Download Action Changes START********************************************/
    void startDownload(String APT_SOURCE_LINK, String downLoadAction){

            if (!isDownloadActive) {
                Log.d(TAG," Downoad action "+downLoadAction);
                downloadTask = new DownloadTask(this);
                if(isOnline()) {
                    setDownLoadAction(downLoadAction);
                }else{
                    showCustomAlert("Not online.","Kindly check internet connectivity");
                }
                isDownloadActive = true;
                downloadTask.execute(APT_SOURCE_LINK);
            }
    }
    /************************************Download Action Changes END**********************************************/
    /************************************DownloadCallback START***************************************************/

    @Override
    public void updateFromDownload(String result) {
        if (result == null || result.length() < 50 || result.equals(NO_RESP_REC)) {
            showCustomAlert("No Result", "Can not fetch data");
            return;
        } else {
            Log.d(TAG,"Result --->" +result);
                readJsonSourceData(result);
                if(categorySet==null) {
                    categorySet = getCategorySet();
                }
                sourceNameList = getSourceNameList();
                mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, sourceNameList));
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        final String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifiConn = networkInfo.isConnected();
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        return networkInfo;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
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
    /**********************************************MENU CHANGES START**************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d("MainActivity","Menu inflater called ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!mDrawerToggle.onOptionsItemSelected(item)){
            String itemsel = item.getTitle().toString();
            Log.d(TAG,"Item Selected =>"+itemsel);
            if(itemsel.equalsIgnoreCase(CATEGORY_ALL)) {
                lastCatSelected=CATEGORY_ALL;
                startDownload(getAllSourceAPILink(), SOURCE_JSON_FILE);
            }else{
                lastCatSelected=itemsel;
                startDownload(getCategoryLink(itemsel), SOURCE_JSON_FILE);
            }
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int sequence = Menu.FIRST;
        Log.d(TAG,"onPrepareOptionsMenu "+categorySet);
        if(categorySet !=null) {
            menu.clear();
            menu.add(0,sequence,Menu.NONE,CATEGORY_ALL);
            Iterator<String> stringIterator = categorySet.iterator();
            while (stringIterator.hasNext()){
                menu.add(0,sequence,Menu.NONE,stringIterator.next());
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /*********************************************MENU CHANGES END****************************************************************/
    /*********************************************CUSTOM ALERT START*********************************************/

    public void showCustomAlert(String alertTitle, String alertMessage){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.activity_dialog_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
    /******************************************API SETUP START*************************************************/
    static interface API{
        final static String KEY = "&apiKey=d725b7db57304510b1952ffc65861d2a";
        final static String ALL_SOURCE_URL ="https://newsapi.org/v1/sources?language=en&country=us";
        final static String CATEGORY_SOURCE_URL ="https://newsapi.org/v1/sources?language=en&country=us&category=";
        final static String ARTICAL_URL ="https://newsapi.org/v1/articles?source=";
    }

    private static String getAllSourceAPILink(){
        String allSrcURL = API.ALL_SOURCE_URL+API.KEY;
        return allSrcURL;
    }
    static String getArticalLink(String source){
        String articalSrcURL = API.ARTICAL_URL+source+API.KEY;
        return articalSrcURL;
    }
    private static String getCategoryLink(String category){
        String categoryURL = API.CATEGORY_SOURCE_URL+category+API.KEY;
        return categoryURL;
    }
    /******************************************API SETUP END**************************************************/
    /***********************************JSON DATA Reading Start*************************************************/
    private List<Source> readJsonSourceData(String jsonData){
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JasonFileHandler jasonFileHandler= new JasonFileHandler(this);
             sourceList =  jasonFileHandler.readSourceData(inputStream,SOURCE_JSON_FILE);
            Log.d(TAG," List "+sourceList.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return sourceList;
    }
    private List<Artical> readJsonArticalData(String jsonData){
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JasonFileHandler jasonFileHandler= new JasonFileHandler(this);
            articalList =  jasonFileHandler.readArticalData(inputStream,ARTICAL_JSON_FILE);
            Log.d(TAG," List "+articalList.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        return articalList;
    }
    /***********************************JSON DATA Reading END*************************************************/
    /***********************************OTHER CHANGES START*************************************************/
    public String getDownLoadAction() {
        return downLoadAction;
    }

    public void setDownLoadAction(String downLoadAction) {
        this.downLoadAction = downLoadAction;
    }

    Set<String> getCategorySet(){
        Set<String> catSet = new HashSet<>();
        if(sourceList!=null){
            /*catSet.add(CATEGORY_ALL);*/
            Iterator<Source> sourceIterator = sourceList.iterator();
            while (sourceIterator.hasNext()){
                catSet.add(sourceIterator.next().getSourceCategory());
            }
        }
       return catSet;
    }

    List<String> getSourceNameList(){
        List<String> sourceNameList;
            sourceNameList = new ArrayList<>();
            sourceNameMap = new HashMap<>();
        if(sourceList !=null){
            Iterator<Source> it = sourceList.iterator();
            while (it.hasNext()){
                Source source=it.next();
                sourceNameList.add(source.getSourceName());
                sourceNameMap.put(source.getSourceName(),source);
            }
        }
        return sourceNameList;
    }

    public List<Fragment> getFragmentList() {
        fragmentList = new ArrayList<Fragment>();
        return fragmentList;
    }

    /***********************************OTHER CHANGES END*************************************************/
    private class PageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

}

