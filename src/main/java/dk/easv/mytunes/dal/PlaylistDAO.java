package dk.easv.mytunes.dal;
//Project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
//Java imports
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Playlist operations
 */
public class PlaylistDAO implements IPlaylistDataAccess {
    private DBConnector dbConnector;
    private SongDAO songDAO;

    public PlaylistDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.songDAO = new SongDAO();
    }

    /**
     * Creates a new playlist in the database
     */
    @Override
    public Playlist createPlaylist(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO Playlists (name) VALUES (?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, playlist.getName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                playlist.setId(rs.getInt(1));
            }
        }
        return playlist;
    }

    /**
     * Retrieves all playlists from the database
     */
    @Override
    public List<Playlist> getAllPlaylists() throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM Playlists ORDER BY name";

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getInt("id"), rs.getString("name"));
                playlist.setSongs(getSongsInPlaylist(playlist.getId()));
                playlists.add(playlist);
            }
        }
        return playlists;
    }

    /**
     * Updates an existing playlist
     */
    @Override
    public void updatePlaylist(Playlist playlist) throws SQLException {
        String sql = "UPDATE Playlists SET name = ? WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playlist.getName());
            stmt.setInt(2, playlist.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a playlist from the database
     */
    @Override
    public void deletePlaylist(int playlistId) throws SQLException {
        String sql = "DELETE FROM Playlists WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all songs in a specific playlist
     */
    @Override
    public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
        List<Song> songs = new ArrayList<>();
        String sql = """
            SELECT s.* FROM Songs s
            INNER JOIN PlaylistSongs ps ON s.id = ps.songId
            WHERE ps.playlistId = ?
            ORDER BY ps.position
            """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("category"),
                        rs.getInt("duration"),
                        rs.getString("filePath")
                ));
            }
        }
        return songs;
    }

    /**
     * Adds a song to a playlist
     */
    @Override
    public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
        // Get the current max position
        int position = getMaxPosition(playlistId) + 1;

        String sql = "INSERT INTO PlaylistSongs (playlistId, songId, position) VALUES (?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.setInt(3, position);
            stmt.executeUpdate();
        }
    }

    /**
     * Removes a song from a playlist
     */
    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "DELETE FROM PlaylistSongs WHERE playlistId = ? AND songId = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();

            // Reorder remaining songs
            reorderPlaylistSongs(playlistId);
        }
    }

    /**
     * Moves a song up in the playlist
     */
    @Override
    public void moveSongUp(int playlistId, int songId) throws SQLException {
        updateSongPosition(playlistId, songId, -1);
    }

    /**
     * Moves a song down in the playlist
     */
    @Override
    public void moveSongDown(int playlistId, int songId) throws SQLException {
        updateSongPosition(playlistId, songId, 1);
    }

    /**
     * Updates the position of a song in the playlist
     */
    private void updateSongPosition(int playlistId, int songId, int direction) throws SQLException {
        Connection conn = dbConnector.getConnection();
        try {
            conn.setAutoCommit(false);

            // Get current position
            String getCurrentPosSql = "SELECT position FROM PlaylistSongs WHERE playlistId = ? AND songId = ?";
            int currentPos;
            try (PreparedStatement stmt = conn.prepareStatement(getCurrentPosSql)) {
                stmt.setInt(1, playlistId);
                stmt.setInt(2, songId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) return;
                currentPos = rs.getInt("position");
            }

            int newPos = currentPos + direction;
            if (newPos < 1) return; // Can't move up from first position

            // Swap positions
            String swapSql = "UPDATE PlaylistSongs SET position = ? WHERE playlistId = ? AND position = ?";
            try (PreparedStatement stmt = conn.prepareStatement(swapSql)) {
                // Move the other song to temp position
                stmt.setInt(1, -1);
                stmt.setInt(2, playlistId);
                stmt.setInt(3, newPos);
                stmt.executeUpdate();

                // Move current song to new position
                stmt.setInt(1, newPos);
                stmt.setInt(2, playlistId);
                stmt.setInt(3, currentPos);
                stmt.executeUpdate();

                // Move other song to old position
                stmt.setInt(1, currentPos);
                stmt.setInt(2, playlistId);
                stmt.setInt(3, -1);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Gets the maximum position in a playlist
     */
    private int getMaxPosition(int playlistId) throws SQLException {
        String sql = "SELECT MAX(position) as maxPos FROM PlaylistSongs WHERE playlistId = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("maxPos");
            }
        }
        return 0;
    }

    /**
     * Reorders playlist songs to have sequential positions
     */
    private void reorderPlaylistSongs(int playlistId) throws SQLException {
        List<Integer> songIds = new ArrayList<>();
        String selectSql = "SELECT songId FROM PlaylistSongs WHERE playlistId = ? ORDER BY position";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                songIds.add(rs.getInt("songId"));
            }
        }

        // Update positions
        String updateSql = "UPDATE PlaylistSongs SET position = ? WHERE playlistId = ? AND songId = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            for (int i = 0; i < songIds.size(); i++) {
                stmt.setInt(1, i + 1);
                stmt.setInt(2, playlistId);
                stmt.setInt(3, songIds.get(i));
                stmt.executeUpdate();
            }
        }
    }
}