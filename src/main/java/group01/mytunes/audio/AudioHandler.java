package group01.mytunes.audio;

import group01.mytunes.dao.interfaces.ISongDAO;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.nio.file.Files;

public class AudioHandler {

    private static final String DATA_DIR = System.getenv("APPDATA") + "/mytunes/";

    /*public AudioHandler(ISongDAO songDAO) {
        String path = "C:/Users/Lasse Emil Hansen/Downloads/Stanley Most - Kom Kom ft. Rune RK.mp3";
        File file = new File(path);
        Media media = new Media(file.getAbsoluteFile().toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.05d);

        mediaPlayer.play();
    }*/

    public AudioHandler(ISongDAO songDAO) {
        /*try {
            byte[] data = getFileAsByteArray();
            System.out.println(data.length);

            System.out.println(DATA_DIR);
            File tempFile = new File(DATA_DIR + "/MYTUNES.song");
            System.out.println(tempFile.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(data);
            fos.close();


            Media media = new Media(tempFile.getAbsoluteFile().toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.05d);
            mediaPlayer.play();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public static void playSong() {

    }

    private byte[] getFileAsByteArray() throws IOException {
        String path = "C:/Users/Lasse Emil Hansen/Downloads/Stanley Most - Kom Kom ft. Rune RK.mp3";
        File file = new File(path);

        return Files.readAllBytes(file.toPath());
    }
}
