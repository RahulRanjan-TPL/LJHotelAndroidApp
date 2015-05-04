package com.longjingtech.ljhotelandroidapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.longjingtech.ljhotelandroidapp.mainmenu.VideoMenuListViewItemAdapter;
import com.longjingtech.ljhotelandroidapp.sysConfig.ChangeWebServerActivity;
import com.longjingtech.ljhotelandroidapp.upgrade.UpgradeInfo;
import com.longjingtech.ljhotelandroidapp.upgrade.UpgradeInfoParser;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SystemConfigActivity extends ActionBarActivity {
    private static final String TAG = SystemConfigActivity.class.getSimpleName();
    private final int UPGRADE_NONEED = 0;
    private final int UPGRADE_CLIENT = 1;
    private final int GET_UPGRADEINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWNLOAD_ERROR = 4;

    private GridView gridView;
    private String[] configNames = {"设置网页地址","设置点播地址","检查软件更新"};
    private int[] imageRes = {R.drawable.sysconfig,R.drawable.sysconfig,R.drawable.sysconfig};

    private String localVersion;
    private UpgradeInfo upgradeInfo;

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
                    case 0: //更改网页IP地址
                        startActivity(new Intent(SystemConfigActivity.this,ChangeWebServerActivity.class));
                        break;

                    case 1: //更改点播服务器的地址
                        break;

                    case 2: //检查软件更新
                        try {
                            localVersion = getAppVersion();
                            Log.e(TAG," ===== local version = " + localVersion);
                            CheckVersionTask checkVersionTask = new CheckVersionTask();
                            new Thread(checkVersionTask).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    default:
                        break;
                }
            }
        });
    }

    private String getAppVersion() throws Exception {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
        return packageInfo.versionName;
    }

    public class CheckVersionTask implements Runnable {
        InputStream inputStream;

        public void run() {
            try {
                String path = getResources().getString(R.string.upgrade_url_server);
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("GET");

                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                }

                upgradeInfo = UpgradeInfoParser.getUpgradeInfo(inputStream);
                if (upgradeInfo.getVersion().equals(localVersion)) {
                    Log.e(TAG,"版本号相同");
                    Message message = new Message();
                    message.what = UPGRADE_NONEED;
                    handler.sendMessage(message);
                } else {
                    Log.e(TAG,"版本号不相同");
                    Message message = new Message();
                    message.what = UPGRADE_CLIENT;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                Message message = new Message();
                message.what = GET_UPGRADEINFO_ERROR;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case UPGRADE_NONEED:
                    Toast.makeText(getApplicationContext(),"不需要更新",Toast.LENGTH_SHORT).show();
                    break;

                case UPGRADE_CLIENT:
                    showUpgradeDialog();
                    break;

                case GET_UPGRADEINFO_ERROR:
                    Toast.makeText(getApplicationContext(),"获取更新信息失败",Toast.LENGTH_LONG).show();
                    break;

                case DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(),"下载新版本失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    protected void showUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本升级");
        builder.setMessage(upgradeInfo.getDescription());
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG,"下载apk，更新");
                downloadApk();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected void downloadApk() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载更新");
        progressDialog.show();

        new Thread() {
            @Override
            public void run() {
                try {
                    File file = com.longjingtech.ljhotelandroidapp.upgrade.DownloadManager.getFileFromServer(upgradeInfo.getUrl(),progressDialog);
                    sleep(3000);
                    installApk(file);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = DOWNLOAD_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);
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
