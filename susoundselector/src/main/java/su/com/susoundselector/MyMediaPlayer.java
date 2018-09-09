package su.com.susoundselector;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class MyMediaPlayer {

    boolean hasInited=false;
    boolean isPlaying=false;
    boolean isRemove=false;
    MediaPlayer mediaPlayer;
    String key;

    public boolean isRemove() {
        return isRemove;
    }

    public void setRemove(boolean remove) {
        isRemove = remove;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public boolean isHasInited() {
        return hasInited;
    }

    public void setHasInited(boolean hasInited) {
        this.hasInited = hasInited;
    }

    public long getDuration(){
        return mediaPlayer.getDuration();
    }

    public long getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void start() {
        mediaPlayer.start();
    }

    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void prepareAsync() {
        mediaPlayer.prepareAsync();
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    public void prepare() throws IOException{
        mediaPlayer.prepare();
    }

    public void setDataSource(Context context, Uri parse) throws IOException{
        mediaPlayer.setDataSource(context,parse);
    }

    public void setAudioStreamType(int audioStreamType) {
        mediaPlayer.setAudioStreamType(audioStreamType);
    }

    public void stop() {
        mediaPlayer.stop();
    }
}
