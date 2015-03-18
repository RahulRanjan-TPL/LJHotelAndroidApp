package com.longjingtech.ljhotelandroidapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longjingtech.ljhotelandroidapp.sys.NetworkUtils;


public class EducationActivity extends ActionBarActivity {

    private static final String TAG = EducationActivity.class.getSimpleName();
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        webView  = (WebView)findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //webView加速
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.addJavascriptInterface(this,"player");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //return super.shouldOverrideUrlLoading(view, url);
                return true;
            }
        });

        webView.loadUrl("http://192.168.1.180:8888/education_program/vodmain.html");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_education, menu);
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
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
        }

        return super.onKeyDown(keyCode,keyEvent);
    }

    @JavascriptInterface
    public void mediaplay(String movideUrl,String movieName,int movieId,int currentPosition){

        Intent intent = new Intent(this,VODMediaPlayerActivity.class);
        intent.putExtra("movieUrl", movideUrl);
        intent.putExtra("movieName",movieName);
        intent.putExtra("movieId",movieId);
        intent.putExtra("currentPosition",currentPosition);

        Log.e(TAG, "movieUrl :" + movideUrl);
        Log.e(TAG,"movieName : " + movieName);
        Log.e(TAG,"movieId : " + String.valueOf(movieId));
        Log.e(TAG,"string currentPosition : " + String.valueOf(currentPosition));

        startActivity(intent);
    }

    @JavascriptInterface
    public String getWifiMacAddress() {
        NetworkUtils networkUtils = new NetworkUtils(EducationActivity.this.getApplicationContext());
        String wifiMacAddress = networkUtils.getWifiMacAddress();
        return wifiMacAddress;
    }

    @JavascriptInterface
    public String getEthernetMacAddress() {
        NetworkUtils networkUtils = new NetworkUtils(EducationActivity.this.getApplicationContext());
        String ethernetMacAddress = networkUtils.getEthernetMacAddress();
        return ethernetMacAddress;
    }

}
