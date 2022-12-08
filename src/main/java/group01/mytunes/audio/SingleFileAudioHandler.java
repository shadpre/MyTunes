package group01.mytunes.audio;

import group01.mytunes.entities.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.util.EmptyStackException;
import java.util.Stack;

public class SingleFileAudioHandler implements IAudioHandler {

    private Media media;

    private boolean isPlaying ;
    private double time;

    private static final String DATA_DIR = System.getenv("APPDATA") + "/MyTunes/songs";
    private double volume = 0.1;

    private Stack<Song> songsPlayed;

    private ISongDAO songDAO;

    private MediaPlayer mediaPlayer;

    /**
     * constructer
     * @param songDAO
     */
    public SingleFileAudioHandler(ISongDAO songDAO) {
        isPlaying = false;
        File dataDirectory = new File(DATA_DIR);
        if(!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
        this.songsPlayed = new Stack<>();

        this.songDAO = songDAO;
        time = 0.0;
    }

    /**
     * makes the mediaPlayer and plays the temparary file
     * @param song
     * @throws MediaException
     */
    public void playSong(Song song) throws MediaException {
        System.out.println("Play:\n" +
                song.getId() + "\n" +
                song.getTitle() + "\n"
        );

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        byte[] songData = songDAO.getSongById(song.getId()).getData();

        try {
            String path = saveTempFile(songData);
            System.out.println("Returned:" + path);
            media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            isPlaying = true;
            mediaPlayer.play();
            songsPlayed.push(song);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MediaException me) {
            throw me;
        }
    }

    /**
     * makkes a temp file to be payed by getind song data as indput.
     * then saves file to a specifik ditectury while its playing
     * @param data
     * @return
     * @throws IOException
     */
    private String saveTempFile(byte[] data) throws IOException {
        System.out.println(DATA_DIR);
        File tempFile = new File(DATA_DIR + "/MYTUNES.song");
        System.out.println("temp file" + tempFile.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data);
        fos.close();

        return tempFile.getAbsoluteFile().toURI().toString();
    }

    /**
     * plays the previous song from the sonPlayed stak and removes the curent song
     * @throws MediaException
     */
    public void playPreviousSong() throws MediaException {
        try {

            songsPlayed.pop();
            Song song = songsPlayed.pop();
            if (song != null) {
                playSong(song);
            }
        } catch (EmptyStackException e){
            System.out.println("No Previous Song");
        }
    }

    /**
     * restarts the song in the mediaPlayer
     */
    @Override
    public void restartSong() {
        mediaPlayer.stop();
        mediaPlayer.play();
    }

    /**
     * changes volume to the given parameter input
     * @param volume
     */
    public void changeVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    /**
     * checks if a song is playing.
     * @return true if its playing a song and flase i not
     * if program is not playing a song it wil play.
     * if progra is playing, it wil pause current song
     */
    @Override
    public boolean playPause() {
        if(mediaPlayer == null) return false;
        if (!isPlaying) {
            mediaPlayer.play();
            isPlaying = true;
        } else {
            mediaPlayer.pause();
            isPlaying = false;
        }

        return isPlaying;
    }


    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public Media getMedia() {
        return media;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * sets the time for next time player plays a song
     * @param songProgress
     */
    public void setTime(double songProgress){
        this.time = songProgress; //sets the time for when the music start
        mediaPlayer.setStartTime(Duration.millis(time * 1000));
    }

    /**
     * Stops current song, and sets isPlaying bollean to false
     */
    public void stop() { //stops music
        this.isPlaying = false;
        mediaPlayer.stop();
    }

    /**
     * starts song and sets is playing to true, after witch it wil st start time to 0 for next song start
     */
    @Override
    public void start() {
        this.isPlaying = true;
        mediaPlayer.play();
        mediaPlayer.setStartTime(Duration.millis(0));
    }
}
