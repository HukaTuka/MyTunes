package dk.easv.mytunes.dal;
//Project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
//Java imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongDAO implements IMytunesDataAccess {

    @Override
    public List<Song> getAllSongs() throws Exception {
        ArrayList<Song> allSongs = new ArrayList<>();
        // try-with-resources
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement())
        {
            String sql = "SELECT * FROM dbo.Songs";

            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {

                //Map DB row to Song object
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String category = rs.getString("Category");
                int duration = rs.getInt("Duration");
                String filePath = rs.getString("FilePath");

                Song song = new Song(id, title, artist, category, duration, filePath);
                allSongs.add(song);
            }
            return allSongs;

        }


        catch (SQLException ex)
        {

            ex.printStackTrace();
            throw new Exception("Could not get songs from database", ex);
        }

    }

    @Override
    public Song createSong(Song song) throws SQLException {
        return null;
    }

    @Override
    public Song getSongById(int id) throws SQLException {
        return null;
    }

    @Override
    public void updateSong(Song song) throws SQLException {

    }

    @Override
    public void deleteSong(int songId) throws SQLException {

    }

    @Override
    public List<Song> searchSongs(String query) throws SQLException {
        return List.of();
    }

    @Override
    public List<Playlist> getAllPlaylists() throws SQLException {
        return List.of();
    }

    @Override
    public Playlist createPlaylist(Playlist newPlaylist) throws SQLException {
        return null;
    }

    @Override
    public void updatePlaylist(Playlist playlist) throws SQLException {

    }

    @Override
    public void deletePlaylist(Playlist playlist) throws SQLException {

    }

}
