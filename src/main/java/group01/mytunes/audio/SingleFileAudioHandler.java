package group01.mytunes.audio;

import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public class SingleFileAudioHandler implements IAudioHandler {

    private Timer timer;

    boolean isPlaying ;

    private static final String DATA_DIR = System.getenv("APPDATA") + "/MyTunes/songs";
    private double volume = 0.1;

    private ISongDAO songDAO;

    private MediaPlayer mediaPlayer;

    public SingleFileAudioHandler(ISongDAO songDAO) {
        isPlaying = false;
        File dataDirectory = new File(DATA_DIR);
        if(!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }

        this.songDAO = songDAO;
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
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            isPlaying = true;
            mediaPlayer.play();
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

    private byte[] getFileAsByteArray() throws IOException {
        String path = "C:/Users/Lasse Emil Hansen/Downloads/Stanley Most - Kom Kom ft. Rune RK.mp3";
        File file = new File(path);

        return Files.readAllBytes(file.toPath());
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


}
