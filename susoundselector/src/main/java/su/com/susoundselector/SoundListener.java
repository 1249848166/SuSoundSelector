package su.com.susoundselector;

import java.util.List;

public interface SoundListener {
    void onPlay(MyMediaPlayer player);
    void onPlaying(MyMediaPlayer player,long progress);
    void onPause(MyMediaPlayer player);
    void onComplete(MyMediaPlayer player);
    void onError(MyMediaPlayer player,String msg);
    void onClear(List<String> paths);
    void onChangeState(MyMediaPlayer player,boolean isOn);
}
