package com.longjingtech.ljhotelandroidapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.longjingtech.ljhotelandroidapp.mainmenu.VideoMenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.sysConfig.ChangeWebServerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SystemConfigActivity extends ActionBarActivity {
    private static final String TAG = SystemConfigActivity.class.getSimpleName();
    private GridView gridView;
    private String[] configNames = {"设置网页地址","设置点播地址","检查软件更新"};
    private int[] imageRes = {R.drawable.sysconfig,R.drawable.sysconfig,R.drawable.sysconfig};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_config);

        gridView = (GridView)findViewById(R.id.gridView);

        List<HashMap<String,Object>> data = new ArrayList<HashMap<String, Object>>();
        int length = configNames.length;
        for (int i = 0;i < length; i++) {
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("ItemImageView",imageRes[i]);
            map.put("ItemTextView",configNames[i]);
            data.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(SystemConfigActivity.this,data,R.layout.system_config_item,new String[]{"ItemImageView","ItemTextView"},new int[]{R.id.item_imageView,R.id.item_textView});
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(SystemConfigActivity.this,ChangeWebServerActivity.class));
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    default:
                        break;
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
