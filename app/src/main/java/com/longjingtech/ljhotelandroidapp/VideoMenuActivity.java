package com.longjingtech.ljhotelandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longjingtech.ljhotelandroidapp.customViews.VideoSlidingMenu;
import com.longjingtech.ljhotelandroidapp.mainmenu.VideoMenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.tools.NetworkUtils;

public class VideoMenuActivity extends ActionBarActivity {
    private WebView webView;
    private ListView listView;
    private static final String TAG = VideoMenuActivity.class.getSimpleName();
    private String[] categoryName;
    private VideoSlidingMenu videoSlidingMenu;
    private String webServerIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_menu);

        videoSlidingMenu = (VideoSlidingMenu)findViewById(R.id.videoSlidingMenu);

        Bundle bundle = getIntent().getExtras();
        categoryName = bundle.getString("movieCategory").split(",");

        listView = (ListView) findViewById(R.id.listView);
        webView = (WebView) findViewById(R.id.webView);

        //listView.setDivider(null);

        SharedPreferences sharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
        webServerIP = sharedPreferences.getString("webServer","192.168.1.240");

        VideoMenuListViewItemAdapter videomenuListViewItemAdapter = new VideoMenuListViewItemAdapter(this,categoryName);
        listView.setAdapter(videomenuListViewItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                webView.loadUrl(webServerIP + ":8888/hotel/index_1.php?type=" + categoryName[position]);
                listView.requestFocus();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this,"player");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //return super.shouldOverrideUrlLoading(view, url);
                return true;
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView webView,KeyEvent keyEvent) {
                //让onKeyDown来处理KeyEvent.KEYCODE_STAR,此处对遥控器应菜单键
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_STAR) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView webView1,String url,Bitmap bitmap) {
                super.onPageStarted(webView1,url,bitmap);
            }

            @Override
            public void onPageFinished(WebView webView,String url) {
                super.onPageFinished(webView,url);
            }
        });

        webView.requestFocus();
        webView.loadUrl(webServerIP + ":8888/hotel/index_1.php?type=all");

        videoSlidingMenu.setScrollEvent(webView);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_STAR:
                if (!videoSlidingMenu.isLeftLayoutVisible()) {
                    videoSlidingMenu.scrollToLeftLayout();
                    listView.requestFocus();
                } else if (videoSlidingMenu.isLeftLayoutVisible()) {
                    videoSlidingMenu.scrollToRightLayout();
                    webView.requestFocus();
                }

                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (videoSlidingMenu.isLeftLayoutVisible()) {
                    videoSlidingMenu.scrollToRightLayout();
                    webView.requestFocus();
                }

                return true;

            case KeyEvent.KEYCODE_BACK:

                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }

                return true;
        }

        return super.onKeyDown(keyCode, event);
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
