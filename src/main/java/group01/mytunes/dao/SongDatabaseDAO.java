package group01.mytunes.dao;

import group01.mytunes.Main;
import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.database.DatabaseConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongDatabaseDAO implements ISongDAO {

    @Override
    public List<Song> getAllSongInfo() {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spGetAllSongInfo";
            Statement statement = connection.createStatement();

            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Song>();

            while(resultSet.next()) {
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
}
