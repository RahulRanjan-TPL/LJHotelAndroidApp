package com.longjingtech.ljhotelandroidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class HotelServiceActivity extends ActionBarActivity {
    private static final String TAG = HotelServiceActivity.class.getSimpleName();
    private EditText editText;
    private Button button;
    private final String requestUrl = "http://220.248.75.36/handapp/PGcardAmtServlet?arg1=";
    private String userCardNum = null,resultBalance = null,htmlResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_service);

        editText = (EditText)findViewById(R.id.traffic_check_edittext);
        button = (Button)findViewById(R.id.traffic_check_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCardNum = editText.getText().toString();
                if (userCardNum.isEmpty()) {
                    Toast.makeText(HotelServiceActivity.this,"卡号不能为空，请重新输入",Toast.LENGTH_SHORT).show();
                }
                else {
                    //清空输入框的内容
                    editText.setText("");

                    //start to check balance
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Looper.prepare();

                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(requestUrl + userCardNum);

                            try {
                                HttpResponse httpResponse = httpClient.execute(httpPost);
                                Log.e(TAG,"retCode: " + httpResponse.getStatusLine().getStatusCode());
                                if (httpResponse.getStatusLine().getStatusCode() == 200) {

                                    htmlResponse = EntityUtils.toString(httpResponse.getEntity(),"utf-8");

                                    /*
                                    * 返回数据格式 null('金额')
                                    * */
                                    resultBalance = htmlResponse.substring(6,htmlResponse.length() - 3);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(HotelServiceActivity.this);
                                    builder.setTitle("查询结果")
                                           .setMessage("卡号  " + userCardNum + "  的余额为  " + resultBalance + "  元")
                                           .setPositiveButton(R.string.traffic_check_yes, new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   dialog.dismiss();
                                               }
                                           }).create().show();

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }).start();

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hotel, menu);
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
