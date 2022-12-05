package group01.mytunes.nextsong;

import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;
import javafx.collections.ObservableList;

import java.util.Random;

public class NextSongFromPlaylistShuffleStrategy implements INextSongStrategy {

    private Random random;

    public ObservableList<PlaylistSong> songList;

    private int nextSongToPlay;

    /**
     * Constructor that sets nextSongToPlay to a random index.
     * @param songList A list containing the songs.
     */
    public NextSongFromPlaylistShuffleStrategy(ObservableList<PlaylistSong> songList) {
        random = new Random();

        this.songList = songList;

        changeSong(getRandomSongIndex());
    }

    /**
     * Consturcot that sets nextSongToPlay to the specified start index.
     * @param songList A list containing the songs.
     * @param startSongIndex The index of the starting song.
     */
    public NextSongFromPlaylistShuffleStrategy(ObservableList<PlaylistSong> songList, int startSongIndex) {
        this(songList);
        changeSong(startSongIndex);
    }

    @Override
    public Song getNextSong() {
        if(songList.size() == 0) return null;
        if(nextSongToPlay > songList.size() - 1) nextSongToPlay = getRandomSongIndex();

        Song toReturn = songList.get(nextSongToPlay).getSong();

        nextSongToPlay = getRandomSongIndex();

        return toReturn;
    }

    @Override
    public void changeSong(int index) {
        this.nextSongToPlay = index;
    }

    /**
     * Generates a random index that is not the same as the index of the song that is playing now.
     * @return The random index.
     */
    private int getRandomSongIndex() {
        if(songList.size() <= 1) return 0;

        int toReturn = Integer.MIN_VALUE;

        while(toReturn < 0 || toReturn != nextSongToPlay) {
            toReturn = random.nextInt(songList.size());
        }

        return toReturn;
    }
}
