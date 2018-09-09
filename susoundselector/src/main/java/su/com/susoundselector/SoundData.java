package su.com.susoundselector;

import java.io.Serializable;

public class SoundData implements Serializable {

    String path;
    String artist;
    String title;
    String duration;

    public SoundData(String path, String artist, String title, String duration) {
        this.path = path;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
