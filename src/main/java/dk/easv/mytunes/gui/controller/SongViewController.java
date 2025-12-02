package dk.easv.mytunes.gui.controller;
//Project imports
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.model.SongModel;
//Java imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;


public class SongViewController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtTitle;
    @FXML private TextField txtArtist;
    @FXML private TextField txtCategory;
    @FXML private TextField txtDuration;
    @FXML private TextField txtFilePath;
    @FXML private Label lblError;

    private SongModel songModel;
    private Song songToEdit;
    private boolean saveClicked = false;

    // Define the data folder path
    private static final String DATA_FOLDER = "data";
    private File dataDirectory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            songModel = new SongModel();

            // Initialize data directory
            dataDirectory = new File(DATA_FOLDER);

            // Create data folder if it doesn't exist
            if (!dataDirectory.exists()) {
                dataDirectory.mkdirs();
                System.out.println("Created data folder at: " + dataDirectory.getAbsolutePath());
            }

        } catch (IOException e) {
            showError("Failed to initialize: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the song to edit (for edit mode)
     */
    public void setSong(Song song) {
        this.songToEdit = song;

        if (song != null) {
            lblTitle.setText("Edit Song");
            txtTitle.setText(song.getTitle());
            txtArtist.setText(song.getArtist());
            txtCategory.setText(song.getCategory());
            txtDuration.setText(String.valueOf(song.getDuration()));
            txtFilePath.setText(song.getFilePath());
        } else {
            lblTitle.setText("New Song");
        }
    }

    /**
     * Sets the song model (called from main controller)
     */
    public void setSongModel(SongModel model) {
        this.songModel = model;
    }

    /**
     * Handle browse button click - restricted to data folder only
     */
    @FXML
    private void handleBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File from Data Folder");

        // Set initial directory to data folder
        if (dataDirectory.exists()) {
            fileChooser.setInitialDirectory(dataDirectory);
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"),
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"),
                new FileChooser.ExtensionFilter("WAV Files", "*.wav"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) txtFilePath.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Validate that file is in data folder
            if (isFileInDataFolder(selectedFile)) {
                // Store relative path from data folder
                String relativePath = getRelativePath(selectedFile);
                txtFilePath.setText(relativePath);

                extractAudioDuration(selectedFile);

            } else {
                showError("Selected file must be in the 'data' folder!\nPlease copy your music files to: " + dataDirectory.getAbsolutePath());
            }
        }
    }

    private void extractAudioDuration(File audioFile){
        MediaPlayer tempPlayer = null;
        try {
            Media media = new Media(audioFile.toURI().toString());
            tempPlayer = new MediaPlayer(media);

            final MediaPlayer player = tempPlayer;
            player.setOnReady(() -> {
                try {

                Duration duration = player.getMedia().getDuration();
                int durationInSeconds = (int) duration.toSeconds();
                txtDuration.setText(String.valueOf(durationInSeconds));
            } catch (Exception e) {
            showError("Failed to extract audio duration: " + e.getMessage());

            if (txtDuration.getText().trim().isEmpty()) {
                txtDuration.setText("180"); // Default to 3 minutes
            }
        }finally {
            player.dispose();
                }
            });

        } catch (Exception e) {
            showError("Failed to create media player for duration extraction" + e.getMessage());

            if (txtDuration.getText().trim().isEmpty()) {
                txtDuration.setText("180"); // 3 minutes default
            }
            if (tempPlayer != null) {
                tempPlayer.dispose();
            }
        }
    }

    /**
     * Handle save button click
     */
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            try {
                String title = txtTitle.getText().trim();
                String artist = txtArtist.getText().trim();
                String category = txtCategory.getText().trim();
                int duration = Integer.parseInt(txtDuration.getText().trim());
                String filePath = txtFilePath.getText().trim();

                if (songToEdit != null) {
                    // Edit existing song
                    songToEdit.setTitle(title);
                    songToEdit.setArtist(artist);
                    songToEdit.setCategory(category);
                    songToEdit.setDuration(duration);
                    songToEdit.setFilePath(filePath);
                    songModel.updateSong(songToEdit);
                } else {
                    // Create new song
                    songModel.createSong(title, artist, category, duration, filePath);
                }

                saveClicked = true;
                closeWindow();

            } catch (NumberFormatException e) {
                showError("Duration must be a valid number");
            } catch (Exception e) {
                showError("Error saving song: " + e.getMessage());
            }
        }
    }

    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        saveClicked = false;
        closeWindow();
    }

    /**
     * Validates the input fields
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (txtTitle.getText().trim().isEmpty()) {
            errors.append("Title is required.\n");
        }

        if (txtArtist.getText().trim().isEmpty()) {
            errors.append("Artist is required.\n");
        }

        if (txtDuration.getText().trim().isEmpty()) {
            errors.append("Duration is required.\n");
        } else {
            try {
                int duration = Integer.parseInt(txtDuration.getText().trim());
                if (duration <= 0) {
                    errors.append("Duration must be positive.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Duration must be a valid number.\n");
            }
        }

        if (txtFilePath.getText().trim().isEmpty()) {
            errors.append("File path is required.\n");
        } else {
            // Validate file is in data folder
            File file = new File(txtFilePath.getText().trim());
            if (!file.exists()) {
                errors.append("Selected file does not exist.\n");
            } else if (!isFileInDataFolder(file)) {
                errors.append("File must be in the 'data' folder.\n");
            }
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }

        lblError.setVisible(false);
        return true;
    }

    /**
     * Checks if a file is within the data folder
     */
    private boolean isFileInDataFolder(File file) {
        try {
            String filePath = file.getCanonicalPath();
            String dataPath = dataDirectory.getCanonicalPath();
            return filePath.startsWith(dataPath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the relative path from the data folder
     */
    private String getRelativePath(File file) {
        try {
            String filePath = file.getCanonicalPath();
            String dataPath = dataDirectory.getCanonicalPath();

            if (filePath.startsWith(dataPath)) {
                // Return path relative to project root (includes "data/")
                return Paths.get("").toAbsolutePath().relativize(file.toPath()).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * Shows error message
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    /**
     * Closes the window
     */
    private void closeWindow() {
        Stage stage = (Stage) txtTitle.getScene().getWindow();
        stage.close();
    }

    /**
     * Returns whether save was clicked
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
}