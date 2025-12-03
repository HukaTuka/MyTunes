package dk.easv.mytunes.gui.model;
//Project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.PlaylistManager;
//Java imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Model layer for Playlist management in the GUI
 * Acts as intermediary between GUI controllers and business logic
 */
public class PlaylistModel {

    private ObservableList<Playlist> playlistsToBeViewed;
    private PlaylistManager playlistManager;

    public PlaylistModel() throws Exception {
        this.playlistManager = new PlaylistManager();
        this.playlistsToBeViewed = FXCollections.observableArrayList();
        loadAllPlaylists();
    }

    /**
     * Gets the observable list of playlists for the UI
     */
    public ObservableList<Playlist> getObservablePlaylists() {
        return playlistsToBeViewed;
    }

    /**
     * Loads all playlists from the database
     */
    public void loadAllPlaylists() throws Exception {
        playlistsToBeViewed.clear();
        playlistsToBeViewed.addAll(playlistManager.getAllPlaylists());
    }

    /**
     * Creates a new playlist
     */
    public void createPlaylist(String name) throws Exception {
        playlistManager.createPlaylist(name);
        loadAllPlaylists();
    }

    /**
     * Updates an existing playlist
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        playlistManager.updatePlaylist(playlist);
        loadAllPlaylists();
    }

    /**
     * Deletes a playlist
     */
    public void deletePlaylist(Playlist playlist) throws Exception {
        playlistManager.deletePlaylist(playlist.getId());
        loadAllPlaylists();
    }

    /**
     * Gets songs in a specific playlist
     */
    public ObservableList<Song> getSongsInPlaylist(Playlist playlist) throws Exception {
        if (playlist == null) {
            return FXCollections.observableArrayList();
        }
        return FXCollections.observableArrayList(
                playlistManager.getSongsInPlaylist(playlist.getId())
        );
    }

    /**
     * Adds a song to a playlist
     */
    public void addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        playlistManager.addSongToPlaylist(playlist.getId(), song.getId());
        loadAllPlaylists();
    }

    /**
     * Removes a song from a playlist
     */
    public void removeSongFromPlaylist(Playlist playlist, Song song) throws Exception {
        playlistManager.removeSongFromPlaylist(playlist.getId(), song.getId());
        loadAllPlaylists();
    }

    /**
     * Moves a song up in the playlist
     */
    public void moveSongUp(Playlist playlist, Song song) throws Exception {
        playlistManager.moveSongUp(playlist.getId(), song.getId());
    }

    /**
     * Moves a song down in the playlist
     */
    public void moveSongDown(Playlist playlist, Song song) throws Exception {
        playlistManager.moveSongDown(playlist.getId(), song.getId());
    }

}