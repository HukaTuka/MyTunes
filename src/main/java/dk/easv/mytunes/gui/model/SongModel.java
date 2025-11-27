package dk.easv.mytunes.gui.model;
//Project imports
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.SongManager;
//Java imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

/**
 * Model layer for Song management in the GUI
 * Acts as intermediary between GUI controllers and business logic
 */
public class SongModel {

    private ObservableList<Song> songsToBeViewed;
    private SongManager songManager;

    public SongModel() throws Exception {
        this.songManager = new SongManager();
        this.songsToBeViewed = FXCollections.observableArrayList();
        loadAllSongs();
    }

    /**
     * Gets the observable list of songs for the UI
     */
    public ObservableList<Song> getObservableSongs() {
        return songsToBeViewed;
    }

    /**
     * Loads all songs from the database
     */
    public void loadAllSongs() throws Exception {
        songsToBeViewed.clear();
        songsToBeViewed.addAll(songManager.getAllSongs());
    }

    /**
     * Creates a new song
     */
    public void createSong(String title, String artist, String category, int duration, String filePath) throws Exception {
        songManager.createSong(title, artist, category, duration, filePath);
        loadAllSongs();
    }

    /**
     * Updates an existing song
     */
    public void updateSong(Song song) throws Exception {
        songManager.updateSong(song);
        loadAllSongs();
    }

    /**
     * Deletes a song
     */
    public void deleteSong(Song song, boolean deleteFile) throws Exception {
        songManager.deleteSong(song.getId(), deleteFile);
        loadAllSongs();
    }

    /**
     * Searches songs by query using database
     */
    public void searchSongs(String query) throws Exception {
        if (query == null || query.trim().isEmpty()) {
            loadAllSongs();
        } else {
            songsToBeViewed.clear();
            songsToBeViewed.addAll(songManager.searchSongs(query));
        }
    }

    /**
     * Gets a song by ID
     */
    public Song getSongById(int id) throws SQLException {
        return songManager.getSongById(id);
    }
}