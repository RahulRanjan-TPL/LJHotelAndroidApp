package com.longjingtech.ljhotelandroidapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.longjingtech.ljhotelandroidapp.customViews.CustomLinkViews;
import com.longjingtech.ljhotelandroidapp.mainmenu.MainmenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.weather.WeatherInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.Calendar;

public class MainmenuActivity extends ActionBarActivity {

    private static final String TAG = MainmenuActivity.class.getSimpleName();
    private ListView listView;
    private TextView textViewDate,textViewToday,textViewDegree,textViewWeather;
    private CustomLinkViews customLinkViews_ethernet,customLinkViews_wifi,customLinkViews_usb;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private USBBroadcastReceiver usbBroadcastReceiver;
    private String categoryName;

    private Calendar calendar;
    private String date;

    private static final String CITYNAME = "上海";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        int[] mIcons = {R.drawable.main_icon_tv,R.drawable.main_icon_vod,R.drawable.main_icon_usb,R.drawable.main_icon_service,R.drawable.main_icon_setting};
        int[] mTexts = {R.string.mainmenu_tv,R.string.mainmenu_vod,R.string.mainmenu_local_play,R.string.mainmenu_hotel_service,R.string.mainmenu_sys_config};

        //从welcome页获取日期和、天气及温度值
        Bundle bundle = getIntent().getExtras();
        String date = bundle.getString("welcome_date");
        String degree = bundle.getString("welcome_degree");
        String weather = bundle.getString("welcome_weather");
        int noWeatherFlag = bundle.getInt("noWeatherFlag");

        textViewDate = (TextView)findViewById(R.id.mainmenu_textView_date);
        //如果MainActivity获取到了日期，则MainMenu共享之，否则重新获取
        if (date != null) {
            textViewDate.setText(date);
        }
        else {
            Runnable runnable = new DateRunner();
            new Thread(runnable).start();
        }

        textViewToday = (TextView)findViewById(R.id.mainmenu_weather_textview_today);
        textViewToday.setText(R.string.mainmenu_weather_today);

        textViewWeather = (TextView)findViewById(R.id.mainmenu_weather_textview);
        textViewDegree = (TextView)findViewById(R.id.mainmenu_weather_textview_degree);

        //如果MainActivity里取得了天气数据，则MainMenu共享之，否则重新获取
        if (noWeatherFlag == 1) {
            textViewDegree.setText(R.string.welcome_weather_none);
        }
        else if ((weather != null) && (degree != null)) {
            textViewWeather.setText(weather);
            textViewDegree.setText(degree);
        }
        else {
            getWeatherFrom36Wu();
        }

        customLinkViews_ethernet = (CustomLinkViews)findViewById(R.id.mainmenu_link_ethernet);
        customLinkViews_ethernet.setTextViewText("NET");
        customLinkViews_ethernet.setImageResource(R.drawable.net_inactive);

        customLinkViews_wifi = (CustomLinkViews)findViewById(R.id.mainmenu_link_wifi);
        customLinkViews_wifi.setTextViewText("WIFI");
        customLinkViews_wifi.setImageResource(R.drawable.net_inactive);

        customLinkViews_usb = (CustomLinkViews)findViewById(R.id.mainmenu_link_usb);
        customLinkViews_usb.setImageResource(R.drawable.net_inactive);
        customLinkViews_usb.setTextViewText("USB");

        listView = (ListView)findViewById(R.id.mainmenu_listview);

        MainmenuListViewItemAdapter mainmenuListViewItemAdapter = new MainmenuListViewItemAdapter(this,mIcons,mTexts);
        listView.setAdapter(mainmenuListViewItemAdapter);

        //去掉ListView每个Item的间隔线
        listView.setDivider(null);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //TV
                        startActivity(new Intent(MainmenuActivity.this,EducationActivity.class));
                        break;

                    case 1: //vod
                        Intent intent = new Intent();
                        intent.setClass(MainmenuActivity.this,VideoMenuActivity.class);

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("movieCategory",categoryName);
                        intent.putExtras(bundle1);

                        startActivity(intent);
                        break;

                    case 2:
                        Intent intent1 = new Intent();
                        intent1.setClass(MainmenuActivity.this,LocalPlayerActivity.class);
                        startActivity(intent1);
                        break;

                    case 3:
                        startActivity(new Intent(MainmenuActivity.this,HotelServiceActivity.class));
                        break;

                    case 4:
                        Toast.makeText(MainmenuActivity.this,"正在建设中...",Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
        });

        listView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        //获取点播分类列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                String language;

                Looper.prepare();
                language = getLocalLanguage();

                HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

                HttpPost httpPost = new HttpPost("http://192.168.1.180:8888/hotel/post_movietype.php?language=" + language);

                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        Log.e(TAG,"Get movie category success.");
                        categoryName = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    }
                    else {
                        Log.e(TAG,"Error response code.");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();

            }
        }).start();

    }

    class DateRunner implements Runnable {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    getDate();
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getDate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    textViewDate = (TextView)findViewById(R.id.welcome_textView_date);

                    calendar = Calendar.getInstance();
                    date = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) +
                            "/" + calendar.get(Calendar.DAY_OF_MONTH);

                    textViewDate.setText(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
    *36wu.com JSON数据格式
    * {"status":200,
    * "message":"OK",
    * "data":{"dateTime":"2015年2月26日","city":"上海","temp":"9℃","minTemp":"6℃","maxTemp":"11℃","weather":"阴转小雨","windDirection":"东北风","windForce":"1级","humidity":"82%","img_1":"2","img_2":"7","refreshTime":"11:43"}}
    **/
    private void getWeatherFrom36Wu() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String cityUrl = "http://api.36wu.com/Weather/GetWeather?district=" + CITYNAME;

                WeatherInfo weatherInfo = new WeatherInfo();
                String weatherJson = weatherInfo.getWeatherInfoByUrl(cityUrl);

                if (weatherJson == null) {
                    Log.e(TAG,"36wu weather json error.");
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(weatherJson);

                        if (jsonObject.getInt("status") != 200) {
                            Log.e(TAG,"w36 weather status != 200");
                            textViewDegree.setText(R.string.welcome_weather_none);
                        }
                        else {

                            JSONObject weatherObject = jsonObject.getJSONObject("data");
                            Message message = new Message();
                            message.obj = weatherObject;
                            handler.sendMessage(message);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("WeatherFrom36wu")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            JSONObject jsonObject = (JSONObject)message.obj;
            try {

                textViewDegree.setText(jsonObject.getString("minTemp") + "~" + jsonObject.getString("maxTemp"));
                textViewWeather.setText(jsonObject.getString("weather"));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public String getLocalLanguage() {

        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");

        return language;
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent1) {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    //Log.e(TAG, "Ethernet is connected.");
                    customLinkViews_ethernet.setImageResource(R.drawable.net_active);
                }
                else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    //Log.e(TAG,"wifi is connected.");
                    customLinkViews_wifi.setImageResource(R.drawable.net_active);
                }
            }
        }
    }

    private class USBBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent1) {
            String action = intent1.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                Log.e(TAG,"usb mounted.");
                customLinkViews_usb.setImageResource(R.drawable.net_active);
            }
            else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                Log.e(TAG,"usb ejected.");
                customLinkViews_usb.setImageResource(R.drawable.net_inactive);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //监听网络
        networkBroadcastReceiver = new NetworkBroadcastReceiver();
        IntentFilter intentFilterNet = new IntentFilter();
        intentFilterNet.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkBroadcastReceiver,intentFilterNet);

        //监听USB设备
        usbBroadcastReceiver = new USBBroadcastReceiver();
        IntentFilter intentFilterUSB = new IntentFilter();
        intentFilterUSB.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilterUSB.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilterUSB.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilterUSB.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilterUSB.addDataScheme("file");
        registerReceiver(usbBroadcastReceiver, intentFilterUSB);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkBroadcastReceiver != null) {
            unregisterReceiver(networkBroadcastReceiver);
        }

        if (usbBroadcastReceiver != null) {
            unregisterReceiver(usbBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}