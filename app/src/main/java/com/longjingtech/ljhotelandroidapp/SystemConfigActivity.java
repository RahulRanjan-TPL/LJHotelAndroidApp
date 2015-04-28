package com.longjingtech.ljhotelandroidapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longjingtech.ljhotelandroidapp.mainmenu.VideoMenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.sysConfig.ChangeWebServerActivity;


public class SystemConfigActivity extends ActionBarActivity {
    private static final String TAG = SystemConfigActivity.class.getSimpleName();
    private ListView listView;
    private String[] configNames = {"网页地址"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_config);

        listView = (ListView)findViewById(R.id.listView);

        VideoMenuListViewItemAdapter videoMenuListViewItemAdapter = new VideoMenuListViewItemAdapter(this,configNames);
        listView.setAdapter(videoMenuListViewItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(SystemConfigActivity.this, ChangeWebServerActivity.class));
                } else if (position == 1) {

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system_config, menu);
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
