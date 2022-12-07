package group01.mytunes.audio;

import group01.mytunes.entities.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import javafx.scene.media.Media;
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

    public void playSong(Song song) {
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
        }
    }

    private String saveTempFile(byte[] data) throws IOException {
        System.out.println(DATA_DIR);
        File tempFile = new File(DATA_DIR + "/MYTUNES.song");
        System.out.println("temp file" + tempFile.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data);
        fos.close();

        return tempFile.getAbsoluteFile().toURI().toString();
    }
    public void playPreviousSong(){
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

    @Override
    public void restartSong() {
        mediaPlayer.stop();
        mediaPlayer.play();
    }

    public void changeVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    @Override
    public boolean playPause() {
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

    public void setTime(double songProgress){
        this.time = songProgress; //sets the time for when the music start
        mediaPlayer.setStartTime(Duration.millis(time * 1000));
    }

    public void stop() { //stops music
        this.isPlaying = false;
        mediaPlayer.stop();
    }

    @Override
    public void start() {
        this.isPlaying = true;
        mediaPlayer.play();
        mediaPlayer.setStartTime(Duration.millis(0));
    }
}
