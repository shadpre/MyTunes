package group01.mytunes.Models;

public class Artist {
    private int id;
    private String Name;

    public Artist(int id, String name) {
        this.id = id;
        Name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "%d %s".formatted(getId(), getName());
    }
}
