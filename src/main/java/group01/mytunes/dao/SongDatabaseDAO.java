package group01.mytunes.dao;

import group01.mytunes.entities.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.exceptions.SQLDeleteException;
import group01.mytunes.exceptions.SQLUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDatabaseDAO implements ISongDAO {

    @Override
    public List<Song> getAllSongInfo() {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllSongInfo;";
            Statement statement = connection.createStatement();

            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Song>();

            while (resultSet.next()) {
                resultList.add(new Song(
                        resultSet.getInt("Id"),
                        resultSet.getString("Title"),
                        null,
                        resultSet.getInt("Playtime")
                ));
            }

            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Song getSongById(int id) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetSongById ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next()) {
                return new Song(
                        id,
                        result.getString("Title"),
                        result.getBytes("Data"),
                        result.getInt("Playtime")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void deleteSong(int id) throws SQLDeleteException {
        if (id < 0) return;

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spDeleteSong ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }

    @Override
    public Song createSong(Song song) {
        if (song == null) return null;
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "spNewSong ?,?,?;";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, song.getTitle());
            statement.setInt(2, song.getPlaytime());
            statement.setBytes(3, song.getData());

            var res = statement.executeQuery();

            if (res.next()) {
                song.setId(res.getInt(1));

                return song;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String getSongDataHash(int id) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spHashSongData ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next()) return result.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void updateSong(Song selectedSong, String newSongName) {
        if (selectedSong == null) return;
        if (newSongName == null) return;

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spUpdateSongTittle ?,?"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, selectedSong.getId());
            statement.setString(2, newSongName); //tittle

            statement.executeUpdate();

            selectedSong.setTitle(newSongName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }

    public List<Integer> getArtistsToSong(int id) {

        List<Integer> artistID = new ArrayList<>();

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {

            String sql = "SELECT * FROM Song_artist_relation WHERE [Songid] = ?";

            //Connnect prepared stametnt to sql
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeQuery();

            //get the ID from DB
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                artistID.add(rs.getInt("ArtistId"));
            }


        } catch (SQLException e) {

        }

        return artistID;
    }
    public List<Integer> getAlbumToSong(int id) {

        List<Integer> albumID = new ArrayList<>();

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {

            String sql = "SELECT * FROM Song_album_relation WHERE [Songid] = ?";

            //Connect prepared stametnt to sql
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeQuery();

            //get the ID from DB
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                albumID.add(rs.getInt("AlbumId"));
            }
        } catch (SQLException e) {

        }

        return albumID;
    }

    @Override
    public void removeSongArtistRelation(int songId, int artistId) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "DELETE FROM [Song_Artist_Relation] WHERE [SongId]=? AND [ArtistId]=?;"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, songId);
            statement.setInt(2, artistId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }

    @Override
    public void changeArtistOnSong(int songId, int oldArtistId, int newArtistId) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "UPDATE [Song_Artist_Relation] SET [ArtistId]=? WHERE [SongId]=? AND [ArtistId]=?;"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, newArtistId);
            statement.setInt(2, songId);
            statement.setInt(3, oldArtistId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }

    @Override
    public void removeSongAlbumRelation(int songId, int albumId) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "DELETE FROM [Song_Album_Relation] WHERE [SongId]=? AND [AlbumId]=?;"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, songId);
            statement.setInt(2, albumId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }

    @Override
    public void changeAlbumOnSong(int songId, int oldAlbumId, int newAlbumId) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "UPDATE [Song_Album_Relation] SET [AlbumId]=? WHERE [SongId]=? AND [AlbumId]=?;"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, newAlbumId);
            statement.setInt(2, songId);
            statement.setInt(3, oldAlbumId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }
}