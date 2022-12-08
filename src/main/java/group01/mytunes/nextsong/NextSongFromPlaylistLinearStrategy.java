package group01.mytunes.nextsong;

import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;
import javafx.collections.ObservableList;

/**
 * Fot this to work, the playlist MUST be sorted in the order that you want to play.
 */
public class NextSongFromPlaylistLinearStrategy implements INextSongStrategy {

    public ObservableList<PlaylistSong> songList;
    private int playingNowIndex;

    public NextSongFromPlaylistLinearStrategy(ObservableList<PlaylistSong> songList, int startSong) {
        this.songList = songList;
        changeSong(startSong); // Minus 1 since it returns the next song.
    }

    @Override
    public Song getNextSong() {
        if(songList.size() == 0) return null;

        playingNowIndex++;

        if(playingNowIndex >= songList.size()) playingNowIndex = 0;

        PlaylistSong songToPlay = songList.get(playingNowIndex);

        return songToPlay;
    }

    @Override
    public void changeSong(int index) {
        this.playingNowIndex = index - 1;
    }
}
