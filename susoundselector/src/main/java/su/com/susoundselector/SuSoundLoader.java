package su.com.susoundselector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuSoundLoader {

    private static SuSoundLoader instance;

    ExecutorService executorService;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                CallbackObj obj = (CallbackObj) msg.obj;
                List<SoundData> list = obj.getSoundDataList();
                LoadSoundCallback callback = obj.getLoadSoundCallback();
                callback.onLoad(list);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private SuSoundLoader(){
        executorService= Executors.newFixedThreadPool(10);
    }

    public static SuSoundLoader getInstance(){
        if(instance==null){
            synchronized (SuSoundLoader.class){
                if(instance==null){
                    instance=new SuSoundLoader();
                }
            }
        }
        return instance;
    }

    public void loadFileSounds(final Context context, final LoadSoundCallback loadSoundCallback) throws NullPointerException{
        try {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        ContentResolver resolver = context.getContentResolver();
                        Cursor cursor = resolver.query(uri, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                        List<SoundData> list = new ArrayList<>();
                        assert cursor != null;
                        while (cursor.moveToNext()) {
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            SoundData soundData = new SoundData(path, artist, title, duration);
                            list.add(soundData);
                        }
                        CallbackObj callbackObj = new CallbackObj(loadSoundCallback, list);
                        Message msg = handler.obtainMessage();
                        msg.obj = callbackObj;
                        handler.sendMessage(msg);
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class CallbackObj{

        LoadSoundCallback loadSoundCallback;
        List<SoundData> soundDataList;

        public CallbackObj(LoadSoundCallback loadSoundCallback, List<SoundData> soundDataList) {
            this.loadSoundCallback = loadSoundCallback;
            this.soundDataList = soundDataList;
        }

        public LoadSoundCallback getLoadSoundCallback() {
            return loadSoundCallback;
        }

        public void setLoadSoundCallback(LoadSoundCallback loadSoundCallback) {
            this.loadSoundCallback = loadSoundCallback;
        }

        public List<SoundData> getSoundDataList() {
            return soundDataList;
        }

        public void setSoundDataList(List<SoundData> soundDataList) {
            this.soundDataList = soundDataList;
        }
    }
}
