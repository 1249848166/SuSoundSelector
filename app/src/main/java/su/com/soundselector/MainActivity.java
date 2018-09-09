package su.com.soundselector;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.List;

import su.com.susoundselector.LoadSoundCallback;
import su.com.susoundselector.MyMediaPlayer;
import su.com.susoundselector.SoundData;
import su.com.susoundselector.SoundListener;
import su.com.susoundselector.SuSoundLoader;
import su.com.susoundselector.SuSoundManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,SoundListener{

    ImageButton play1,play2,play3;
    SeekBar bar1,bar2,bar3;

    MyMediaPlayer player1;
    MyMediaPlayer player2;
    MyMediaPlayer player3;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                ImageObj imageObj = (ImageObj) msg.obj;
                ImageView imageView = imageObj.getImageView();
                int imageResource = imageObj.getImageResource();
                imageView.setImageResource(imageResource);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SuSoundLoader.getInstance().loadFileSounds(this,new LoadSoundCallback() {
                @Override
                public void onLoad(List<SoundData> datas) {
                    for (SoundData soundData : datas) {
                        System.out.println("标题：" + soundData.getTitle() + "作家：" + soundData.getArtist() +
                                "时长：" + soundData.getDuration() + "路径：" + soundData.getPath());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        play1=findViewById(R.id.play1);
        play2=findViewById(R.id.play2);
        play3=findViewById(R.id.play3);

        bar1=findViewById(R.id.bar1);
        bar2=findViewById(R.id.bar2);
        bar3=findViewById(R.id.bar3);

        player1 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_NETWORK,
                "http://bmob-cdn-21427.b0.upaiyun.com/2018/09/07/51f701f3402aae8e807634b54421923a.wav",this);
        player2 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_LOCAL,
                R.raw.sound1 + "",this);
        player3 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_FILE,
                Environment.getExternalStorageDirectory() + "/aaa.wav",this);

        play1.setOnClickListener(this);
        play2.setOnClickListener(this);
        play3.setOnClickListener(this);

        bar1.setOnSeekBarChangeListener(this);
        bar2.setOnSeekBarChangeListener(this);
        bar3.setOnSeekBarChangeListener(this);

    }

    //clicklistener
    @Override
    public void onClick(View v) {
        int i= v.getId();
        switch (i){
            case R.id.play1:
                SuSoundManager.getInstance().playOrPauseNetworkSoundAtProgress(player1, (int) player1.getCurrentPosition());
                break;
            case R.id.play2:
                SuSoundManager.getInstance().playOrPauseLocalSoundAtProgress(player2, (int) player2.getCurrentPosition());
                break;
            case R.id.play3:
                SuSoundManager.getInstance().playOrPauseFileSoundAtProgress(player3, (int) player3.getCurrentPosition());
                break;
        }
    }

    //seekbar的listener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int i=seekBar.getId();
        switch (i){
            case R.id.bar1:
                bar1.setMax((int) player1.getDuration());
                player1.seekTo(bar1.getProgress());
                break;
            case R.id.bar2:
                bar2.setMax((int) player2.getDuration());
                player2.seekTo(bar2.getProgress());
                break;
            case R.id.bar3:
                bar3.setMax((int) player3.getDuration());
                player3.seekTo(bar3.getProgress());
                break;
        }
    }

    //soundmanager的listener
    @Override
    public void onPlay(MyMediaPlayer player) {
        System.out.println("开始播放");
    }

    @Override
    public void onPlaying(MyMediaPlayer player, long progress) {
        System.out.println("正在播放");
        if(player.hashCode()==player1.hashCode()){
            bar1.setMax((int) player1.getDuration());
            bar1.setProgress((int) player1.getCurrentPosition());
        }else if(player.hashCode()==player2.hashCode()){
            bar2.setMax((int) player2.getDuration());
            bar2.setProgress((int) player2.getCurrentPosition());
        }else if(player.hashCode()==player3.hashCode()){
            bar3.setMax((int) player3.getDuration());
            bar3.setProgress((int) player3.getCurrentPosition());
        }
    }

    @Override
    public void onPause(MyMediaPlayer player) {
        System.out.println("暂停播放");
    }

    @Override
    public void onComplete(MyMediaPlayer player) {
        System.out.println("结束播放");
        if(player.hashCode()==player1.hashCode()){
            bar1.setProgress(0);
        }else if(player.hashCode()==player2.hashCode()){
            bar2.setProgress(0);
        }else if(player.hashCode()==player3.hashCode()){
            bar3.setProgress(0);
        }
    }

    @Override
    public void onError(MyMediaPlayer player, String msg) {
        System.out.println("播放错误：" + msg);
    }

    @Override
    public void onClear(List<String> paths) {
        for(String p:paths){
            System.out.println("删除了:"+p);
        }
    }

    @Override
    public void onChangeState(MyMediaPlayer player, boolean isOn) {
        ImageView imageView = null;
        int imageResource;
        if(isOn){
            imageResource=R.drawable.pause;
            if(player.hashCode()==player1.hashCode()){
                imageView=play1;
            }else if(player.hashCode()==player2.hashCode()){
                imageView=play2;
            }else if(player.hashCode()==player3.hashCode()){
                imageView=play3;
            }
        }else{
            imageResource=R.drawable.play;
            if(player.hashCode()==player1.hashCode()){
                imageView=play1;
            }else if(player.hashCode()==player2.hashCode()){
                imageView=play2;
            }else if(player.hashCode()==player3.hashCode()){
                imageView=play3;
            }
        }
        ImageObj imageObj=new ImageObj(imageView,imageResource);
        Message msg=handler.obtainMessage();
        msg.obj=imageObj;
        handler.sendMessage(msg);
    }

    class ImageObj{

        ImageView imageView;
        int imageResource;

        public ImageObj(ImageView imageView, int imageResource) {
            this.imageView = imageView;
            this.imageResource = imageResource;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getImageResource() {
            return imageResource;
        }

        public void setImageResource(int imageResource) {
            this.imageResource = imageResource;
        }
    }
}
