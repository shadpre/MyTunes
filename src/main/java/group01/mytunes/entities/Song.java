package group01.mytunes.entities;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private int id;
    private String title;
    private byte[] data;
    private int playtime;

    public Song(int id, String title, byte[] data, int playtime) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.playtime = playtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song other = (Song) o;
        return this.id == other.getId();
    }
}
