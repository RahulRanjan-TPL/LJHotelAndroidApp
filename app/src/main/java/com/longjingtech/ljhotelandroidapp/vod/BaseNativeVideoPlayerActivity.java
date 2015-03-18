package com.longjingtech.ljhotelandroidapp.vod;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.longjingtech.ljhotelandroidapp.R;
import com.longjingtech.ljhotelandroidapp.sys.NetworkUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Activity，原生MediaPlayer视频播放器页面基类。
 * 如果要实现自定义UI的视频播放器页面，只需要继承该类，在此基础上实现一个{@link NativeMediaController.MediaControllerGenerator}接口即可。
 */
public class BaseNativeVideoPlayerActivity extends Activity
        implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        NativeMediaController.MediaPlayerControl, NativeMediaController.MediaControllerGenerator {

    private static final String TAG = BaseNativeVideoPlayerActivity.class.getSimpleName();
    private SurfaceView videoSurface;
    protected MediaPlayer mPlayer;
    protected NativeMediaController mController;
    protected Intent mIntent;
    private SurfaceHolder mVideoHolder;
    private int mCurrentPosition,mTotalTime,mMovieId;
    private String mMovieName,wifiMacAddress,ethMacAddress;
    private AudioManager audioManager;
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_native_activity);

        mIntent = getIntent();
        mMovieName = mIntent.getStringExtra("movieName");
        mMovieId = mIntent.getIntExtra("movieId",0);
        mCurrentPosition = mIntent.getIntExtra("currentPosition",0);

        /*
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
        */

        videoSurface = (SurfaceView) findViewById(R.id.video_surface);
        mVideoHolder = videoSurface.getHolder();
        mVideoHolder.addCallback(this);

        mController = new NativeMediaController(this);
        mController.setUIGenerator(this);

        audioManager = (AudioManager)this.getSystemService(AUDIO_SERVICE);
    }


    @Override
    public BaseMediaControllerHolder generateMediaController() {
        return new BaseMediaControllerHolder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // 创建一个MediaPlayer对象
            mPlayer = new MediaPlayer();

            if (mIntent.getStringExtra("movieUrl").isEmpty()) {
                Toast.makeText(BaseNativeVideoPlayerActivity.this,"影片地址不存在",Toast.LENGTH_SHORT).show();
                finish();
            }

            // 设置播放的视频数据源
            mPlayer.setDataSource(this, Uri.parse(mIntent.getStringExtra("movieUrl")));

            System.out.println(mIntent.getStringExtra("movieUrl"));

            // 设置AudioStreamType
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 将视频输出到SurfaceView
            mPlayer.setDisplay(mVideoHolder);
            // 播放准备，使用异步方式，配合OnPreparedListener
            mPlayer.prepareAsync();
            // 设置相关的监听器
            mPlayer.setOnPreparedListener(this);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(BaseNativeVideoPlayerActivity.this,"影片播放完毕,谢谢欣赏!",Toast.LENGTH_SHORT).show();
                    mPlayer.stop();
                    finish();
                }
            });

            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(BaseNativeVideoPlayerActivity.this,"影片播放出现错误,请重新点播!",Toast.LENGTH_LONG).show();
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    finish();
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    // Implement VideoMediaCController.MediaPlayerControl
    @Override
    public void start() {
        if (mPlayer != null)
            mPlayer.start();
    }

    @Override
    public void pause() {
        if(mPlayer != null)
        mPlayer.pause();
    }

    @Override
    public int getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        } else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        } else
            return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mPlayer != null) {
            mPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
    }
    // End Implement VideoMediaCController.MediaPlayerControl

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        mController.setMediaPlayer(this);
        mController.setAnchorView((FrameLayout) findViewById(R.id.video_surface_container));
        mPlayer.start();
        mTotalTime = mPlayer.getDuration();
        this.seekTo(mCurrentPosition);
        if (mCurrentPosition != 0) {
            Toast.makeText(BaseNativeVideoPlayerActivity.this,"从头播放，请连续按2次方向左键!",Toast.LENGTH_LONG).show();
        }
        mController.show();
        mController.updatePausePlay();
    }
    // End MediaPlayer.OnPreparedListener

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = this.getCurrentPosition();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", mCurrentPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent) {

        Log.e(TAG,"===keyCode= " + keyCode);

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mPlayer.isPlaying() == true) {
                    mPlayer.pause();
                    mController.show();
                } else {
                    mPlayer.start();
                    mController.hide();
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mPlayer.isPlaying() == true) {
                    mCurrentPosition = mPlayer.getCurrentPosition();
                    if (mCurrentPosition - 5000 <= 0) {
                        seekTo(0);
                    }
                    else {
                        seekTo(mCurrentPosition - 5000);
                    }
                    mController.show();
                }
                keyEvent.startTracking();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mPlayer.isPlaying() == true) {
                    mCurrentPosition = mPlayer.getCurrentPosition();
                    if (mCurrentPosition + 15000 >= mTotalTime){
                        seekTo(mTotalTime);
                    }
                    else {
                        seekTo(mCurrentPosition + 15000);
                    }
                    mController.show();
                }
                keyEvent.startTracking();
                return true;


            case KeyEvent.KEYCODE_BACK:

                mPlayer.pause();

                CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(BaseNativeVideoPlayerActivity.this);
                builder.setTitle(R.string.vod_exit_title)
                       .setMessage(R.string.vod_exit_msg)
                       .setNegativeButton(R.string.vod_exit_no,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mPlayer.start();
                            }
                        })
                       .setPositiveButton(R.string.vod_exit_ok,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mCurrentPosition = mPlayer.getCurrentPosition();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        NetworkUtils networkUtils = new NetworkUtils(BaseNativeVideoPlayerActivity.this.getApplicationContext());
                                        wifiMacAddress = networkUtils.getWifiMacAddress();
                                        ethMacAddress = networkUtils.getEthernetMacAddress();

                                        HttpClient httpClient = new DefaultHttpClient();

                                        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
                                        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

                                        HttpPost httpPost = new HttpPost("http://192.168.1.180:8888/hotel/insert_play_time.php");

                                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                                        //将本地播放进度及其标志信息上传给服务器
                                        params.add(new BasicNameValuePair("mac",ethMacAddress));
                                        params.add(new BasicNameValuePair("movieId",String.valueOf(mMovieId)));
                                        params.add(new BasicNameValuePair("currentPosition",String.valueOf(mCurrentPosition)));
                                        params.add(new BasicNameValuePair("totalTime",String.valueOf(mTotalTime)));

                                        try {
                                            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                                            HttpResponse httpResponse = httpClient.execute(httpPost);

                                            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                                Log.e(TAG,"Send movieId&mac&curPosition code 200.");
                                            }
                                            else {
                                                Log.e(TAG,"Send movieId&mac&curPosition other response code.");
                                            }
                                        }catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:

                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
        }

        return super.onKeyDown(keyCode,keyEvent);
    }

    @Override
    public boolean onKeyUp(int keyCode,KeyEvent keyEvent) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 800) {
                firstTime = secondTime;
                return true;
            }
            else {

                mPlayer.seekTo(0);
                mPlayer.start();
                mController.show();
                mController.updatePausePlay();
                return true;
            }
        }

        return super.onKeyUp(keyCode,keyEvent);
    }

    @Override
    public boolean onKeyLongPress(int keyCode,KeyEvent keyEvent) {
        return super.onKeyLongPress(keyCode,keyEvent);
    }

}
