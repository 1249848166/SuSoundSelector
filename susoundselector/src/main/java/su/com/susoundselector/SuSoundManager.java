package su.com.susoundselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//说明：几个状态：开始，暂停，错误，结束都是在子线程中，因而在回调中不能直接更改UI，正在播放状态处于主线程，可以直接更改UI
public class SuSoundManager {

    public static final String PARAM_LOCAL="local";
    public static final String PARAM_FILE="file";
    public static final String PARAM_NETWORK="network";

    static SuSoundManager instance;
    SoundListener soundListener;
    Map<String,MyMediaPlayer> map=new HashMap<>();

    public static SuSoundManager getInstance(){
        if(instance==null){
            synchronized (SuSoundManager.class){
                if(instance==null){
                    instance=new SuSoundManager();
                }
            }
        }
        return instance;
    }

    ExecutorService executorService;
    Handler handler;

    @SuppressLint("HandlerLeak")
    private SuSoundManager(){
        executorService= Executors.newFixedThreadPool(10);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(soundListener!=null) {
                    for (MyMediaPlayer p : map.values()) {
                        if(p.isPlaying())
                            soundListener.onPlaying(p, p.getCurrentPosition());
                    }
                }
                loopUpdate();
            }
        };
        handler.sendEmptyMessageDelayed(0, 500);
    }

    void loopUpdate(){
        handler.sendEmptyMessageDelayed(0,500);
    }

    public MyMediaPlayer getMediaPlayer(Context context, String way, String param, final SoundListener soundListener){
        this.soundListener=soundListener;
        final MyMediaPlayer player=new MyMediaPlayer();
        if(way.equals(PARAM_LOCAL)){
            int res=Integer.valueOf(param);
            player.setMediaPlayer(MediaPlayer.create(context,res));
        }else if(way.equals(PARAM_FILE)){
            player.setMediaPlayer(new MediaPlayer());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(context, Uri.parse(param));// "file://mnt/sdcard/xxx/xxx.mp3"
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(way.equals(PARAM_NETWORK)){
            player.setMediaPlayer(new MediaPlayer());
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(context,Uri.parse(param));// "http://www.xxx.com/xxx,map3"
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        player.setKey(param);
        player.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(soundListener!=null) {
                    soundListener.onComplete(player);
                    soundListener.onChangeState(player,false);
                }
                player.setPlaying(false);
            }
        });
        player.getMediaPlayer().setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(soundListener!=null) {
                    switch (what){
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            soundListener.onError(player,"未知错误");
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            soundListener.onError(player,"服务器崩溃");
                            break;
                    }
                    switch (extra){
                        case MediaPlayer.MEDIA_ERROR_IO:
                            soundListener.onError(player,"读取错误");
                            break;
                        case MediaPlayer.MEDIA_ERROR_MALFORMED:
                            soundListener.onError(player,"格式错误");
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                            soundListener.onError(player,"类型不支持");
                            break;
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                            soundListener.onError(player,"读取超时");
                            break;
                    }
                }
                player.setPlaying(false);
                return true;
            }
        });
        map.put(player.getKey(),player);
        return map.get(player.getKey());
    }

    public void playOrPauseLocalSoundAtProgress(MyMediaPlayer mediaPlayer,int progress){
        playOrPauseAtProgress(mediaPlayer,progress);
    }

    public void playOrPauseFileSoundAtProgress(final MyMediaPlayer mediaPlayer, final int progress){
        if(!mediaPlayer.isHasInited()) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playOrPauseAtProgress(mediaPlayer, progress);
                }
            });
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setHasInited(true);
        }else{
            playOrPauseAtProgress(mediaPlayer, progress);
        }
    }

    public void playOrPauseNetworkSoundAtProgress(final MyMediaPlayer mediaPlayer, final int progress){
        if(!mediaPlayer.isHasInited()) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playOrPauseAtProgress(mediaPlayer, progress);
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setHasInited(true);
        }else{
            playOrPauseAtProgress(mediaPlayer, progress);
        }
    }

    private void playOrPauseAtProgress(final MyMediaPlayer mediaPlayer, final int progress){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.pause();
                    if(soundListener!=null) {
                        soundListener.onPause(mediaPlayer);
                        soundListener.onChangeState(mediaPlayer,false);
                    }
                    mediaPlayer.setPlaying(false);
                }else{
                    mediaPlayer.seekTo(progress);
                    mediaPlayer.start();
                    if(soundListener!=null) {
                        soundListener.onPlay(mediaPlayer);
                        soundListener.onChangeState(mediaPlayer,true);
                    }
                    mediaPlayer.setPlaying(true);
                }
            }
        });
    }

    public void clear(MyMediaPlayer player){
        if(soundListener!=null){
            List<String> list=new ArrayList<>();
            list.add(player.getKey());
            soundListener.onClear(list);
        }
        if(player!=null&&map.containsValue(player)) {
            player.setRemove(true);
            player.stop();
            map.remove(player);
        }
    }

    public void clearAll(){
        if(soundListener!=null){
            List<String> list = new ArrayList<>();
            for(String key:map.keySet()){
                list.add(key);
                map.get(key).stop();
                map.get(key).setRemove(true);
            }
            soundListener.onClear(list);
        }
        map.clear();
    }

}
