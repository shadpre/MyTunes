package group01.mytunes.entities;

public class PlaylistSong {

    private int relationId;
    private Song song;

    public PlaylistSong(int relationId, Song song) {
        this.relationId = relationId;
        this.song = song;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public String toString() {
        return getSong().toString();
    }
}
