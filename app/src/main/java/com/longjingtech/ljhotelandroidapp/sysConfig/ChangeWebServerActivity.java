package com.longjingtech.ljhotelandroidapp.sysConfig;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.longjingtech.ljhotelandroidapp.R;

public class ChangeWebServerActivity extends ActionBarActivity {
    private static final String TAG = ChangeWebServerActivity.class.getSimpleName();
    private TextView textView;
    private EditText editText;
    private Button button_return,button_save;
    private String webServerAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_web_server);

        textView = (TextView)findViewById(R.id.textView);
        textView.setText(R.string.title_textview_change_web_server);

        editText = (EditText)findViewById(R.id.editText);

        button_return = (Button)findViewById(R.id.button_return);
        button_save = (Button)findViewById(R.id.button_save);

        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webServerAddr = editText.getText().toString();
                if (webServerAddr.isEmpty()) {
                    Toast.makeText(ChangeWebServerActivity.this, "IP地址不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setText("");
                    SharedPreferences prefs = getSharedPreferences("config", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("webServer", webServerAddr);
                    editor.commit();
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_web_server, menu);
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
