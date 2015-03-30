package com.longjingtech.ljhotelandroidapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.io.IOException;


public class Mp3PlayerActivity extends ActionBarActivity {

    private static final String TAG = Mp3PlayerActivity.class.getSimpleName();
    private ImageView imageView;
    private String filePath;
    private MediaPlayer mediaPlayer;
    private MP3File mp3File;
    private TextView textView_header,textView_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_player);

        filePath = getIntent().getStringExtra("audioPath");

        textView_header = (TextView)findViewById(R.id.mp3_player_textview_head);
        textView_content = (TextView)findViewById(R.id.mp3_player_textview_content);
        imageView = (ImageView)findViewById(R.id.mp3_player_imageview);

        mp3PlayerGetHead();
        mp3PlayerGetContent();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(Mp3PlayerActivity.this, "音乐播放完毕,谢谢欣赏!", Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
                finish();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(Mp3PlayerActivity.this,"音乐播放出现错误,请重新播放!",Toast.LENGTH_LONG).show();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                finish();

                return true;
            }
        });
    }

    void mp3PlayerGetContent() {
        try {
            if (mp3File.hasID3v1Tag()) {
                Tag tag = mp3File.getTag();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("歌手: " + tag.getFirst(FieldKey.ARTIST) + "\n");
                stringBuffer.append("专辑名: " + tag.getFirst(FieldKey.ALBUM) + "\n");
                stringBuffer.append("歌名: " + tag.getFirst(FieldKey.TITLE) + "\n");
                stringBuffer.append("时间: " + tag.getFirst(FieldKey.YEAR) + "\n");

                Artwork artwork = tag.getFirstArtwork();
                byte[] byteArray = artwork.getBinaryData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                imageView.setImageBitmap(bitmap);
                textView_content.setText(stringBuffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    void mp3PlayerGetHead() {
        try {
            File file = new File(filePath);
            mp3File = (MP3File)AudioFileIO.read(file);
            MP3AudioHeader mp3AudioHeader = mp3File.getMP3AudioHeader();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("长度: " + mp3AudioHeader.getTrackLength() + "\n");
            stringBuffer.append("比特率: " + mp3AudioHeader.getBitRate() + "\n");
            stringBuffer.append("编码器: " + mp3AudioHeader.getEncoder() + "\n");
            stringBuffer.append("格式: " + mp3AudioHeader.getFormat() + "\n");
            stringBuffer.append("声道: " + mp3AudioHeader.getChannels() + "\n");
            stringBuffer.append("采样率: " + mp3AudioHeader.getSampleRate() + "\n");
            stringBuffer.append("MPEG: " + mp3AudioHeader.getMpegLayer() + "\n");
            stringBuffer.append("MP3起始字节: " + mp3AudioHeader.getMp3StartByte() + "\n");
            stringBuffer.append("精确的长度: " + mp3AudioHeader.getPreciseTrackLength() + "\n");
            stringBuffer.append("帧数: " + mp3AudioHeader.getNumberOfFrames() + "\n");
            stringBuffer.append("编码类型: " + mp3AudioHeader.getEncodingType() + "\n");
            stringBuffer.append("MPEG版本: " + mp3AudioHeader.getMpegVersion() + "\n");

            textView_header.setText(stringBuffer);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mp3_player, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                mediaPlayer.stop();
                finish();
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                else {
                    mediaPlayer.start();
                }

                return true;

            default:
                break;
        }

        return super.onKeyDown(keyCode,keyEvent);
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
