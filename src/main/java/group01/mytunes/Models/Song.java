package group01.mytunes.Models;

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
}
