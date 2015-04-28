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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.longjingtech.ljhotelandroidapp.customViews.CustomLinkViews;
import com.longjingtech.ljhotelandroidapp.weather.WeatherInfo;
import com.longjingtech.ljhotelandroidapp.welcome.ListViewItemAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private TextView textView_welcome,textViewDate,textView_welcome_weather_today,textView_welcome_weather_degree,textView_welcome_weather;
    private ListViewItemAdapter listViewItemAdapter;
    private Locale locale;
    private String language;
    private String date,welcome_weather_degree,welcome_weather;
    private Calendar calendar;
    private Intent intent;
    private Bundle bundle;
    private int weatherInfoFlag = 0;

    private CustomLinkViews customLinkViews_ethernet,customLinkViews_wifi,customLinkViews_usb;

    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private USBBroadcastReceiver usbBroadcastReceiver;

    private static final String CITYNAME = "上海";
    private static final String CITYCODE = "101020100"; //for Shanghai

    private static final String welcome_en = "Dear Mr/Mrs xxx\n\n\t\tWelcome to xxx hotel,we are glad to see you.The hotel is " +
            "extremely well equipped and the only one five-star hotel in our city.We will provide delicious local and known oversea " +
            "food,and on the other hand comfortable accommodation is also supply to you during the journey.Please contact us if you " +
            "have any problems.Thank you!";

    private static final String welcome_zh = "尊敬的xx先生/女士\n\n\t\t您好!欢迎光临xxx大酒店,很高兴能为您服务!本店是本市唯一一家五星级大酒店," +
            "坐落于市中心区域,交通极其便利,店内设施齐全,装备精良.在您入住期间我们将提供美味的本地美食和知名的海外食品,除此之外,也将提供舒适的住宿条件," +
            "消除您的旅途疲劳.如有问题请及时联系我们.再次感谢您的光临!";

    private static final String welcome_weather_today_en = "Today";
    private static final String welcome_weather_today_zh = "今日天气:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_welcome = (TextView)findViewById(R.id.welcome_textView);
        textView_welcome_weather_today = (TextView)findViewById(R.id.welcome_weather_textview_today);
        textView_welcome_weather_degree = (TextView)findViewById(R.id.welcome_weather_textview_degree);
        textView_welcome_weather = (TextView)findViewById(R.id.welcome_weather_textview);

        //从中国天气网上抓取当日天气数据
        //getWeather();

        //从http://api.36wu.com上抓取当日天气数据
        getWeatherFrom36Wu();

        //获取当前日期
        Runnable runnable = new DateRunner();
        new Thread(runnable).start();

        customLinkViews_ethernet = (CustomLinkViews)findViewById(R.id.welcome_link_ethernet);
        customLinkViews_ethernet.setTextViewColor(R.color.black);
        customLinkViews_ethernet.setTextViewText("NET");
        customLinkViews_ethernet.setImageResource(R.drawable.net_inactive);

        customLinkViews_wifi = (CustomLinkViews)findViewById(R.id.welcome_link_wifi);
        customLinkViews_wifi.setTextViewColor(R.color.black);
        customLinkViews_wifi.setTextViewText("WIFI");
        customLinkViews_wifi.setImageResource(R.drawable.net_inactive);

        customLinkViews_usb = (CustomLinkViews)findViewById(R.id.welcome_link_usb);
        customLinkViews_usb.setTextViewColor(R.color.black);
        customLinkViews_usb.setImageResource(R.drawable.net_inactive);
        customLinkViews_usb.setTextViewText("USB");

        intent = new Intent();
        intent.setClass(MainActivity.this,MainmenuActivity.class);
        bundle = new Bundle();

        listView = (ListView)findViewById(R.id.welcome_listview);
        listViewItemAdapter = new ListViewItemAdapter();
        listView.setAdapter(listViewItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //中文
                        language = "zh";
                        break;

                    case 1: //英文
                        language = "en";
                        break;

                    default:
                        break;
                }

                //设置App语言环境
                changeLang(language);

                //向MainmenuActivity发送日期及当天温度数据
                bundle.putString("welcome_date",date);
                bundle.putString("welcome_degree",welcome_weather_degree);
                bundle.putString("welcome_weather",welcome_weather);
                bundle.putInt("noWeatherFlag",weatherInfoFlag);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: //Chinese
                        textView_welcome.setText(welcome_zh);
                        textView_welcome_weather_today.setText(welcome_weather_today_zh);
                        break;

                    case 1: //English
                        textView_welcome.setText(welcome_en);
                        textView_welcome_weather_today.setText(welcome_weather_today_en);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //获取系统语言设置
        loadLocale();
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

    /*
    中国天气网的天气JSON数据格式
    {
        "weatherinfo":
        {
            "city": "北京", // 城市中文名
            "cityid": "101010100", // 城市ID
            "temp1": "32℃", // 最高温度
            "temp2": "21℃", // 最低温度
            "weather": "阴转晴", // 天气
            "img1": "n2.gif", // 天气图标编号
            "img2": "d0.gif", // 天气图标编号
            "ptime": "18:00" // 发布时间
        }
    }
    */
    private void getWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                String cityUrl ="http://www.weather.com.cn/data/cityinfo/" + CITYCODE + ".html";
                //String cityUrl ="http://m.weather.com.cn/atad/" + CITYCODE + ".html";

                String weatherJson = getWeatherFromUrl(cityUrl);

                if (weatherJson == null) {
                    Log.e(TAG,"weather json error.");
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(weatherJson);
                        JSONObject weatherObject = jsonObject.getJSONObject("weatherinfo");
                        Message message = new Message();
                        message.obj = weatherObject;
                        handler.sendMessage(message);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Looper.loop();
            }
        }).start();
    }

    /*
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            JSONObject jsonObject = (JSONObject)message.obj;
            try {
                welcome_weather_degree = jsonObject.getString("temp1") + "~" + jsonObject.getString("temp2");

                imageView_welcome_weather_info.setImageResource(R.drawable.weather_sunny);
                textView_welcome_weather_degree.setText(welcome_weather_degree);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    */

    /*
    *36wu.com 天气JSON数据格式
    * {"status":200,
    * "message":"OK",
    * "data":{"dateTime":"2015年2月26日","city":"上海","temp":"9℃","minTemp":"6℃","maxTemp":"11℃","weather":"阴转小雨","windDirection":"东北风","windForce":"1级","humidity":"82%","img_1":"2","img_2":"7","refreshTime":"11:43"}}
    **/
    private void getWeatherFrom36Wu() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                String cityUrl = "http://api.36wu.com/Weather/GetWeather?district=" + CITYNAME + "&format=json";

                WeatherInfo weatherInfo = new WeatherInfo();
                String weatherJson = weatherInfo.getWeatherInfoByUrl(cityUrl);

                Log.e(TAG,"===djstava 36wu: " + weatherJson);

                if (weatherJson == null) {
                    Log.e(TAG,"36wu weather json error.");
                    Message message = new Message();
                    message.obj = null;
                    handler.sendMessage(message);
                    weatherInfoFlag = 1;
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(weatherJson);

                            JSONObject weatherObject = jsonObject.getJSONObject("data");
                            Message message = new Message();
                            message.obj = weatherObject;
                            handler.sendMessage(message);

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Looper.loop();
            }
        }).start();
    }

    @SuppressLint("WeatherFrom36wu")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            JSONObject jsonObject = (JSONObject) message.obj;

            if (jsonObject == null) {
                textView_welcome_weather_degree.setText(R.string.welcome_weather_none);
            }
            else {
                try {
                    welcome_weather_degree = jsonObject.getString("minTemp") + "~" + jsonObject.getString("maxTemp");
                    welcome_weather = jsonObject.getString("weather");

                    textView_welcome_weather.setText(welcome_weather);
                    textView_welcome_weather_degree.setText(welcome_weather_degree);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private String getWeatherFromUrl(String url) {
        HttpGet httpGet = new HttpGet(url);

        String result = null;

        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                return result;
            }
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        locale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,Intent intent1) {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    Log.e(TAG,"Ethernet is connected.");
                    customLinkViews_ethernet.setImageResource(R.drawable.net_active);
                }
                else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.e(TAG,"wifi is connected.");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
