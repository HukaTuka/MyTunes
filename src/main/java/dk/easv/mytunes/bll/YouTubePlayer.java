package dk.easv.mytunes.bll;

import dk.easv.mytunes.MyTunesMain;
import dk.easv.mytunes.gui.controller.YouTubePlayerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class YouTubePlayer {

    /**
     * Opens the YouTube audio player in a new window
     */
    public void open() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyTunesMain.class.getResource("views/YouTubePlayer.fxml"));


            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("YouTube Player");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading YouTube player FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens the YouTube audio player with a pre-loaded URL
     */
    public void open(String youtubeUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/YouTubePlayer.fxml"));
            Parent root = loader.load();

            // Get controller and set URL
            YouTubePlayerController controller = loader.getController();
            controller.txtUrl.setText(youtubeUrl);

            Stage stage = new Stage();
            stage.setTitle("YouTube  Player");
            stage.setScene(new Scene(root, 800, 500));
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading YouTube player FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}