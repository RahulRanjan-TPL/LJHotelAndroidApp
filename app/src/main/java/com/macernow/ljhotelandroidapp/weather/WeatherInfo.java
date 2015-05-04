package com.macernow.ljhotelandroidapp.weather;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by djstava on 15/2/26.
 * GET方式获取天气数据信息
 */
public class WeatherInfo {
    public static final String TAG = WeatherInfo.class.getSimpleName();

    public WeatherInfo() {

    }

    /*
    * 功能: 根据传入的url来获取天气信息
    * 参数: String url
    * 返回值: String,JSON数据格式
    **/
    public String getWeatherInfoByUrl(String url) {
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

}
