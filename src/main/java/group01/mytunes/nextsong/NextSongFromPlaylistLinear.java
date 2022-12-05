package group01.mytunes.nextsong;

import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;
import javafx.collections.ObservableList;

/**
 * Fot this to work, the playlist MUST be sorted in the order that you want to play.
 */
public class NextSongFromPlaylistLinear implements INextSongStrategy {

    public ObservableList<PlaylistSong> songList;
    int playingNowIndex;

    public NextSongFromPlaylistLinear(ObservableList<PlaylistSong> songList, int startSong) {
        this.songList = songList;
        changeSong(startSong); // Minus 1 since it returns the next song.
    }

    @Override
    public Song getNextSong() {
        if(songList.size() == 0) return null;

        playingNowIndex++;
        PlaylistSong songToPlay = songList.get(playingNowIndex);

        if(songToPlay == null) songToPlay = songList.stream().findFirst().get();

        return songToPlay.getSong();
    }

    @Override
    public void changeSong(int index) {
        this.playingNowIndex = index - 1;
    }
}
