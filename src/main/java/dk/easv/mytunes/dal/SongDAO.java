package dk.easv.mytunes.dal;
//Project imports
import dk.easv.mytunes.be.Song;
//Java imports
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Song operations
 */
public class SongDAO implements ISongDataAccess {
    private DBConnector dbConnector;

    public SongDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
    }

    /**
     * Creates a new song in the database
     */
    @Override
    public Song createSong(Song song) throws SQLException {
        String sql = "INSERT INTO Songs (title, artist, category, duration, filePath) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getCategory());
            stmt.setInt(4, song.getDuration());
            stmt.setString(5, song.getFilePath());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                song.setId(rs.getInt(1));
            }
        }
        return song;
    }

    /**
     * Retrieves all songs from the database
     */
    @Override
    public List<Song> getAllSongs() throws SQLException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM Songs ORDER BY title";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                songs.add(createSongFromResultSet(rs));
            }
        }
        return songs;
    }

    /**
     * Retrieves a single song by ID
     */
    @Override
    public Song getSongById(int id) throws SQLException {
        String sql = "SELECT * FROM Songs WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createSongFromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Updates an existing song
     */
    @Override
    public void updateSong(Song song) throws SQLException {
        String sql = "UPDATE Songs SET title = ?, artist = ?, category = ?, duration = ?, filePath = ? WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getCategory());
            stmt.setInt(4, song.getDuration());
            stmt.setString(5, song.getFilePath());
            stmt.setInt(6, song.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a song from the database
     */
    @Override
    public void deleteSong(int songId) throws SQLException {
        String sql = "DELETE FROM Songs WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, songId);
            stmt.executeUpdate();
        }
    }

    /**
     * Searches songs by title or artist
     */
    @Override
    public List<Song> searchSongs(String query) throws SQLException {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM Songs WHERE title LIKE ? OR artist LIKE ? ORDER BY title";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                songs.add(createSongFromResultSet(rs));
            }
        }
        return songs;
    }

    /**
     * Helper method to create Song object from ResultSet
     */
    private Song createSongFromResultSet(ResultSet rs) throws SQLException {
        return new Song(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("artist"),
                rs.getString("category"),
                rs.getInt("duration"),
                rs.getString("filePath")
        );
    }
}