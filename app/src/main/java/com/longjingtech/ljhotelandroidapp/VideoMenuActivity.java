package com.longjingtech.ljhotelandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.longjingtech.ljhotelandroidapp.mainmenu.VideoMenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.sys.NetworkUtils;

public class VideoMenuActivity extends ActionBarActivity {
    private WebView mWebView;
    private ListView listView;
    private static final String TAG = VideoMenuActivity.class.getSimpleName();
    private String[] categoryName;

    DisplayMetrics displayMetrics = new DisplayMetrics();
    private View.OnFocusChangeListener onFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Bundle bundle = getIntent().getExtras();
        categoryName = bundle.getString("movieCategory").split(",");

        listView = (ListView) findViewById(R.id.movielist);
        mWebView = (WebView) findViewById(R.id.webView);

        ViewGroup.LayoutParams listparams = listView.getLayoutParams();
        listparams.width = displayMetrics.widthPixels/9;

        //listView.setPadding(64,100,0,0);
        listView.setLayoutParams(listparams);
        listView.setDivider(null);

        //listView.setBackgroundColor(Color.argb(255,128,128,128));

        VideoMenuListViewItemAdapter videomenuListViewItemAdapter = new VideoMenuListViewItemAdapter(this,categoryName);
        listView.setAdapter(videomenuListViewItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i)
                {
                    case 0:
                        break;
                    case 1:
                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                        params.width = 0;
                        listView.setLayoutParams(params);
                        break;
                    case 2:
                        ViewGroup.LayoutParams params1 = listView.getLayoutParams();
                        params1.width = displayMetrics.widthPixels/8;
                        listView.setLayoutParams(params1);
                        break;
                }
            }
        });

        listView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {

            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mWebView.loadUrl("http://192.168.1.180:8888/hotel/index_1.php?type=" + categoryName[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutparams.setMargins(0, 0, 0, 0);
        mWebView.setLayoutParams(layoutparams);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.addJavascriptInterface(this,"player");

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //return super.shouldOverrideUrlLoading(view, url);
                return true;
            }
        });

        //mWebView.setFocusable(false);
        listView.setFocusable(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String str_i = String.valueOf(keyCode);
        Log.e(TAG,str_i);
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                //if (mWebView .canGoBack()) {
                //    mWebView.goBack();
                //    return true;
                //}
                String match = new String("index_1.php");
                String url = mWebView.getUrl();
                Log.e(TAG,url);
                if(url.indexOf(match) == -1)
                {
                    mWebView.goBack();
                    return true;
                }
                if (listView.getWidth() == 0) {
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.width = displayMetrics.widthPixels/8;
                    listView.setLayoutParams(params);
                    mWebView.setFocusable(false);
                    mWebView.setFocusableInTouchMode(false);
                    listView.setFocusable(true);
                    listView.setFocusableInTouchMode(true);
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        // TODO Auto-generated method stub
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    if(params.width != 0) {
                        params.width = 0;
                        listView.setLayoutParams(params);
                        listView.setFocusable(false);
                        listView.setFocusableInTouchMode(false);
                        mWebView.setFocusable(true);
                        mWebView.setFocusableInTouchMode(true);
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @JavascriptInterface
    public void mediaplay(String movideUrl,String movieName,int movieId,int currentPosition){

        Intent intent = new Intent(this,VODMediaPlayerActivity.class);
        intent.putExtra("movieUrl", movideUrl);
        intent.putExtra("movieName",movieName);
        intent.putExtra("movieId",movieId);
        intent.putExtra("currentPosition",currentPosition);

        Log.e(TAG,"movieUrl :" + movideUrl);
        Log.e(TAG,"movieName : " + movieName);
        Log.e(TAG,"movieId : " + String.valueOf(movieId));
        Log.e(TAG,"string currentPosition : " + String.valueOf(currentPosition));

        startActivity(intent);
    }

    @JavascriptInterface
    public String getWifiMacAddress() {
        NetworkUtils networkUtils = new NetworkUtils(VideoMenuActivity.this.getApplicationContext());
        String wifiMacAddress = networkUtils.getWifiMacAddress();
        return wifiMacAddress;
    }

    @JavascriptInterface
    public String getEthernetMacAddress() {
        NetworkUtils networkUtils = new NetworkUtils(VideoMenuActivity.this.getApplicationContext());
        String ethernetMacAddress = networkUtils.getEthernetMacAddress();
        return ethernetMacAddress;
    }
}
