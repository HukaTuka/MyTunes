package dk.easv.mytunes.bll;
//Project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.IPlaylistDataAccess;
import dk.easv.mytunes.dal.PlaylistDAO;
//Java imports
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Business Logic Layer for Playlist operations
 */
public class PlaylistManager {
    private IPlaylistDataAccess playlistDAO;

    public PlaylistManager() throws IOException {
        this.playlistDAO = new PlaylistDAO();
    }

    /**
     * Creates a new playlist with validation
     */
    public Playlist createPlaylist(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }

        Playlist playlist = new Playlist(name);
        return playlistDAO.createPlaylist(playlist);
    }

    /**
     * Retrieves all playlists
     */
    public List<Playlist> getAllPlaylists() throws SQLException {
        return playlistDAO.getAllPlaylists();
    }

    /**
     * Updates an existing playlist
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        if (playlist.getId() <= 0) {
            throw new IllegalArgumentException("Invalid playlist ID");
        }
        if (playlist.getName() == null || playlist.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }

        playlistDAO.updatePlaylist(playlist);
    }

    /**
     * Deletes a playlist
     */
    public void deletePlaylist(int playlistId) throws SQLException {
        if (playlistId <= 0) {
            throw new IllegalArgumentException("Invalid playlist ID");
        }
        playlistDAO.deletePlaylist(playlistId);
    }

    /**
     * Gets songs in a specific playlist
     */
    public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
        return playlistDAO.getSongsInPlaylist(playlistId);
    }

    /**
     * Adds a song to a playlist
     */
    public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
        if (playlistId <= 0 || songId <= 0) {
            throw new IllegalArgumentException("Invalid playlist or song ID");
        }
        playlistDAO.addSongToPlaylist(playlistId, songId);
    }

    /**
     * Removes a song from a playlist
     */
    public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        if (playlistId <= 0 || songId <= 0) {
            throw new IllegalArgumentException("Invalid playlist or song ID");
        }
        playlistDAO.removeSongFromPlaylist(playlistId, songId);
    }

    /**
     * Moves a song up in the playlist
     */
    public void moveSongUp(int playlistId, int songId) throws SQLException {
        playlistDAO.moveSongUp(playlistId, songId);
    }

    /**
     * Moves a song down in the playlist
     */
    public void moveSongDown(int playlistId, int songId) throws SQLException {
        playlistDAO.moveSongDown(playlistId, songId);
    }
}