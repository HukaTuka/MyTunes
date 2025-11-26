package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;

import java.sql.SQLException;
import java.util.List;

public class PlaylistDAO implements IPlaylistDataAccess {
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
