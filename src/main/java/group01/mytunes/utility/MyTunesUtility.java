package group01.mytunes.utility;

import group01.mytunes.entities.Song;
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

    /**
     * converts a byte object to its hex value and returns it as a String
     * @param hash byte object
     * @return a string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * Converts a boolean in seconds to time in minets and seconds
     * @param inputSeconds input seconds as a double
     * @return a string with minets and secons as return
     */
    public static String timeFormatConverter(double inputSeconds){
        int min = (int) (inputSeconds/60);
        int sec = (int) (inputSeconds%60);

        return "%s:%s".formatted(
            (min < 10) ? "0" + min : min,
            (sec < 10) ? "0" + sec : sec
        );
    }
}
