package dk.easv.mytunes.bll;
//Project imports
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ISongDataAccess;
import dk.easv.mytunes.dal.SongDAO;
//Java imports
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Business Logic Layer for Song operations
 */
public class SongManager {
    private ISongDataAccess songDAO;
    private static final String DATA_FOLDER = "data";

    public SongManager() throws IOException {
        this.songDAO = new SongDAO();

        // Ensure data folder exists
        File dataDir = new File(DATA_FOLDER);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Creates a new song with validation
     */
    public Song createSong(String title, String artist, String category, int duration, String filePath) throws Exception {
        // Validation
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist cannot be empty");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        // Check if file exists
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        // Validate file is in data folder
        if (!isFileInDataFolder(file)) {
            throw new IllegalArgumentException("Music files must be located in the 'data' folder. Please move your file to: " + new File(DATA_FOLDER).getAbsolutePath());
        }

        // Check file extension
        String extension = getFileExtension(filePath);
        if (!extension.equalsIgnoreCase("mp3") && !extension.equalsIgnoreCase("wav")) {
            throw new IllegalArgumentException("Only MP3 and WAV files are supported");
        }

        Song song = new Song(title, artist, category, duration, filePath);
        return songDAO.createSong(song);
    }

    /**
     * Retrieves all songs
     */
    public List<Song> getAllSongs() throws SQLException {
        return songDAO.getAllSongs();
    }

    /**
     * Updates an existing song
     */
    public void updateSong(Song song) throws Exception {
        if (song.getId() <= 0) {
            throw new IllegalArgumentException("Invalid song ID");
        }

        // Validation
        if (song.getTitle() == null || song.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (song.getArtist() == null || song.getArtist().trim().isEmpty()) {
            throw new IllegalArgumentException("Artist cannot be empty");
        }

        // Validate file path is in data folder
        File file = new File(song.getFilePath());
        if (!isFileInDataFolder(file)) {
            throw new IllegalArgumentException("Music files must be located in the 'data' folder");
        }

        songDAO.updateSong(song);
    }

    /**
     * Deletes a song
     */
    public void deleteSong(int songId) throws SQLException {
        if (songId <= 0) {
            throw new IllegalArgumentException("Invalid song ID");
        }
        songDAO.deleteSong(songId);
    }

    /**
     * Deletes a song and optionally its file
     */
    public void deleteSong(int songId, boolean deleteFile) throws Exception {
        if (deleteFile) {
            Song song = songDAO.getSongById(songId);
            if (song != null) {
                File file = new File(song.getFilePath());
                if (file.exists() && isFileInDataFolder(file)) {
                    if (!file.delete()) {
                        throw new Exception("Failed to delete file: " + song.getFilePath());
                    }
                }
            }
        }
        deleteSong(songId);
    }

    /**
     * Searches songs by query
     */
    public List<Song> searchSongs(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return getAllSongs();
        }
        return songDAO.searchSongs(query);
    }

    /**
     * Gets a song by ID
     */
    public Song getSongById(int id) throws SQLException {
        return songDAO.getSongById(id);
    }

    /**
     * Checks if a file is within the data folder
     */
    private boolean isFileInDataFolder(File file) {
        try {
            File dataDir = new File(DATA_FOLDER);
            String filePath = file.getCanonicalPath();
            String dataPath = dataDir.getCanonicalPath();
            return filePath.startsWith(dataPath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Helper method to get file extension
     */
    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filePath.length() - 1) {
            return filePath.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Gets the data folder path
     */
    public static String getDataFolderPath() {
        return new File(DATA_FOLDER).getAbsolutePath();
    }
}