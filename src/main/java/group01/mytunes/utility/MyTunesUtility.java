package group01.mytunes.utility;

import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.ISongDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyTunesUtility {

    /**
     * Takes a song WITH data and compares the hash of the song data to the data in the DB.
     * @param song The song with data you wish to verify.
     * @param songDAO The songDAO to use.
     * @return Truf if equal, false otherwise.
     */
    public static boolean compareHashFromDatabaseAndSong(Song song, ISongDAO songDAO) {
        try {
            var messageDigest = MessageDigest.getInstance("MD5");

            byte[] res = messageDigest.digest(song.getData());

            String localHash = bytesToHex(res);

            String dbHash = songDAO.getSongDataHash(song.getId());

            return dbHash.equals(localHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    public static String timeFormatConverter(double inputSeconds){
        int min = (int) (inputSeconds/60);
        int sec = (int) (inputSeconds%60);

        return "%s:%s".formatted(
                (min < 10) ? "0" + min : min,
                (sec < 10) ? "0" + sec : sec
        );
    }

    public static List<Song> search(List<Song> searchInThisList, String query) {
        List<Song> searchResult = new ArrayList<>();

        for (Song song : searchInThisList) {
            if(compareSongTittle(query, song))
            {
                searchResult.add(song);
            }
        }

        return searchResult;
    }


    private static boolean compareSongTittle(String query, Song song) {
        return song.getTitle().toLowerCase().contains(query.toLowerCase());
    }

}
