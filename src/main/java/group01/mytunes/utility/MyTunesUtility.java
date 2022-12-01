package group01.mytunes.utility;

import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.ISongDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        int min = 0;
        int sec = 0;
        int total = (int) inputSeconds;

        String minut = "";
        String seconds = "";

        min = total/60;
        sec = total%60;

        if (min < 10) {
            minut = "0"+min;
        } else  {
            minut = min + "";
        }

        if (sec < 10) {
            seconds = "0"+sec;
        } else {
            seconds = sec + "";
        }

        return minut + ":" + seconds;
    }
}
