package group01.mytunes.Models;

import java.util.Date;

public class Playlist {
    private int id;
    private int userId;
    private int name;
    private Date date;

    public Playlist(int id, int userId, int name, Date date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
