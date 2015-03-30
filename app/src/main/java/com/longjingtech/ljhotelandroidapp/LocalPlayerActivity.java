package com.longjingtech.ljhotelandroidapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.longjingtech.ljhotelandroidapp.adapter.AdapterTextview;
import com.longjingtech.ljhotelandroidapp.sys.CustomFileNameFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LocalPlayerActivity extends ActionBarActivity {

    private static final String TAG = LocalPlayerActivity.class.getSimpleName();
    private TextView textView_deviceName,textView_deviceCapacity;
    private ListView listView;
    private AdapterTextview adapterTextview;

    private List<String> fileList,fileListPath;
    private String[] files,filesPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_player);

        textView_deviceName = (TextView)findViewById(R.id.localplayer_device_name);
        textView_deviceCapacity = (TextView)findViewById(R.id.localplayer_device_capacity);

        listView = (ListView)findViewById(R.id.localplayer_device_file_list_listview);

        fileList = new ArrayList<String>();
        fileListPath = new ArrayList<String>();

        /*
        try {

            //在COS下，系统会在/mnt/udisk/下创建一个随便目录供U盘挂载

            File file = new File("/mnt/udisk");
            if (file.length() == 0) {
                Log.e(TAG,"No Device found.");
            }
            else {
                File[] temp = file.listFiles();
                File tempFile = new File(temp[0].getAbsolutePath());

                //计算U盘总容量、已使用容量、可用容量
                StatFs statFs = new StatFs(tempFile.getPath());
                long blockSize = statFs.getBlockSize();
                long totalBlocks = statFs.getBlockCount();
                long availableBlocks = statFs.getAvailableBlocks();

                String usedSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),(totalBlocks - availableBlocks) * blockSize);
                String availableSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),availableBlocks * blockSize);
                String totalSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),totalBlocks * blockSize);

                textView_deviceCapacity.setText(usedSize + "  可用/共: " + totalSize);

                //罗列支持打开的文件格式
                CustomFileNameFilter videoFileNameFilter = new CustomFileNameFilter();
                videoFileNameFilter.addType(".mp4");
                videoFileNameFilter.addType(".mkv");
                videoFileNameFilter.addType(".avi");
                videoFileNameFilter.addType(".mp3");
                videoFileNameFilter.addType(".doc");
                videoFileNameFilter.addType(".docx");
                videoFileNameFilter.addType(".ppt");
                videoFileNameFilter.addType(".pdf");
                videoFileNameFilter.addType(".xls");
                videoFileNameFilter.addType(".xlsx");

                File[] files1 = tempFile.listFiles(videoFileNameFilter);
                for (int i = 0;i < files1.length;i++) {
                    fileList.add(files1[i].getName());
                    fileListPath.add(files1[i].getPath());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //for BESTV
        try {

            File file = new File("/mnt/udisk");
            if (file.length() == 0) {
                Log.e(TAG,"No File found.");
            }
            else {

                //计算U盘总容量、已使用容量、可用容量
                StatFs statFs = new StatFs("/mnt/udisk");
                long blockSize = statFs.getBlockSize();
                long totalBlocks = statFs.getBlockCount();
                long availableBlocks = statFs.getAvailableBlocks();

                String usedSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),(totalBlocks - availableBlocks) * blockSize);
                String availableSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),availableBlocks * blockSize);
                String totalSize = android.text.format.Formatter.formatFileSize(getApplicationContext(),totalBlocks * blockSize);

                textView_deviceCapacity.setText(usedSize + "  可用/共: " + totalSize);

                //罗列支持打开的文件格式
                CustomFileNameFilter customFileNameFilter = new CustomFileNameFilter();
                customFileNameFilter.addType(".mp4");
                customFileNameFilter.addType(".mkv");
                customFileNameFilter.addType(".avi");
                customFileNameFilter.addType(".mp3");
                customFileNameFilter.addType(".doc");
                customFileNameFilter.addType(".docx");
                customFileNameFilter.addType(".ppt");
                customFileNameFilter.addType(".pdf");
                customFileNameFilter.addType(".xls");
                customFileNameFilter.addType(".xlsx");

                File[] files1 = file.listFiles(customFileNameFilter);
                for (int i = 0;i < files1.length;i++) {
                    fileList.add(files1[i].getName());
                    fileListPath.add(files1[i].getPath());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        files = fileList.toArray(new String[1]);
        filesPath = fileListPath.toArray(new String[1]);

        adapterTextview = new AdapterTextview(this,files);
        listView.setAdapter(adapterTextview);
        if (fileList.isEmpty() == false) {
            listView.setSelector(R.drawable.main_icon_focus);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                * Office文档利用第三方APP WPS来打开，目前这种方式在COS平台上不起作用
                * */
                if (filesPath[position].endsWith("xls") || filesPath[position].endsWith("xlsx") || filesPath[position].endsWith("ppt") || filesPath[position].endsWith("doc") || filesPath[position].endsWith("docx")) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("cn.wps.moffice_eng","cn.wps.moffice.main.local.home.PadHomeActivity");

                    Uri uri = Uri.fromFile(new File(filesPath[position]));
                    intent.setData(uri);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }

                }else if (filesPath[position].endsWith("mp3")) {
                    Intent intent = new Intent();
                    intent.setClass(LocalPlayerActivity.this,Mp3PlayerActivity.class);
                    intent.putExtra("audioPath",filesPath[position]);
                    startActivity(intent);

                }else if (filesPath[position].endsWith("pdf")) {

                }
                else {

                    Intent intent = new Intent();
                    intent.setClass(LocalPlayerActivity.this, VODMediaPlayerActivity.class);
                    intent.putExtra("movieUrl", filesPath[position]);
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_player, menu);
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
