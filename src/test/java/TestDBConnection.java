import group01.mytunes.Main;
import group01.mytunes.Models.Album;
import group01.mytunes.Models.Artist;
import group01.mytunes.Models.Playlist;
import group01.mytunes.dao.AlbumDatabaseDAO;
import group01.mytunes.dao.ArtistDatabaseDAO;
import group01.mytunes.dao.PlaylistDatabaseDAO;
import group01.mytunes.dao.interfaces.IAlbumDAO;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestDBConnection {

    private IArtistDAO artistDAO;
    private IPlaylistDAO playlistDAO;

    private IAlbumDAO albumDAO;

    @BeforeEach
    public void setup() throws IOException {

        var url = Main.class.getResource("config.properties");

        Properties props = new Properties();
        try(InputStream input = url.openStream()) {
            props.load(input);
        }

        var dbIp = props.getProperty("DB_IP");
        var dbPort = Integer.parseInt(props.getProperty("DB_PORT"));
        var dbName= props.getProperty("DB_NAME");
        var dbUsername = props.getProperty("DB_USERNAME");
        var dbPassword = props.getProperty("DB_PASSWORD");

        DatabaseConnectionHandler.init(dbIp, dbPort, dbName, dbUsername, dbPassword);

        artistDAO = new ArtistDatabaseDAO();
        playlistDAO = new PlaylistDatabaseDAO();
        albumDAO = new AlbumDatabaseDAO();
    }

    @Test
    public void TestDatabaseConnection() throws SQLException {
        var connection = DatabaseConnectionHandler.getInstance().getConnection();

        assertTrue(!connection.isClosed());
    }

    @Test
    public void TestGetAllArtists() {
        var result = artistDAO.getArtists();

        for (Artist artist : result) {
            System.out.println(artist);
        }
    }

    @Test
    public void TestGetArtistById() {
        int id = 1;

        Artist result = artistDAO.getArtistById(id);

        System.out.println(result);
    }

    @Test
    public void TestInsertArtist() {
        String name = "Kim Larsen";

        var result = artistDAO.createArtist(name);

        assertTrue(result != null);

        System.out.println("Created artist\n" + result);
    }

    @Test
    public void TestDeleteArtist() {
        int artistToDelete = 2;

        artistDAO.deleteArtist(artistToDelete);
    }

    @Test
    public void TestUpdateArtist() {
        Artist a = artistDAO.getArtists().get(0);

        assertNotNull(a);

        String newName = "Ukendt Kunstner";

        artistDAO.updateArtist(a, newName);

        assertEquals(newName, a.getName());
    }

    @Test
    public void TestGetAllPlaylists() {
        var result = playlistDAO.getPlaylists();

        for(Playlist p : result) {
            System.out.println(p);
        }
    }

    @Test
    public void TestCreatePlaylist() {
        String name = "Den aller bedste!";

        var res = playlistDAO.createPlaylist(name);
        System.out.println(res);
    }

    @Test
    public void TestDeletePlaylist() {
        int id = 3;

        playlistDAO.deletePlaylist(id);
    }

    @Test
    public void TestUpdatePlaylist() {
        Playlist p = playlistDAO.getPlaylists().get(0);

        assertNotNull(p);

        String newName = "Ukendt Liste";

        playlistDAO.updatePlaylist(p, newName);

        assertEquals(newName, p.getName());
    }

    /*
     * Albums
     */

    @Test
    public void TestGetAllAlbums() {
        var result = albumDAO.getAlbums();

        for(Album album : result) {
            System.out.println(album.getId() + " " + album.getName());
        }
    }

    @Test
    public void TestUpdateAlbum() {
        var album = albumDAO.getAlbums().get(0);
        albumDAO.updateAlbum(album, "Midt Om Natten");
    }
}
