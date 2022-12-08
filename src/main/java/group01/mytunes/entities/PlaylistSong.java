package group01.mytunes.entities;

public class PlaylistSong extends Song {
    private int relationId;
    private int position;

    public PlaylistSong(int relationId, Song song, int position) {
        super(song.getId(), song.getTitle(), song.getData(), song.getPlaytime());
        this.relationId = relationId;
        this.position = position;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
