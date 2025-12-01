package dk.easv.mytunes.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;

public class YouTubePlayerController {

    @FXML public TextField txtUrl;
    @FXML private WebView webView;
    @FXML private Label lblStatus;

    private WebEngine engine;

    @FXML
    private void initialize() {
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        // Try to get some debugging
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("WebView state: " + newState);
            if (newState == Worker.State.SUCCEEDED) {
                lblStatus.setText("Video player loaded successfully");
            } else if (newState == Worker.State.FAILED) {
                lblStatus.setText("Failed to load player");
                System.err.println("WebView failed to load");
            }
        });
    }

    @FXML
    private void onPlayAudio() {
        String url = txtUrl.getText().trim();

        if (url.isEmpty()) {
            lblStatus.setText("Please enter a YouTube URL");
            return;
        }

        if (!url.startsWith("http")) {
            url = "https://" + url;
        }

        String videoId = extractVideoId(url);

        if (videoId == null || videoId.isEmpty()) {
            lblStatus.setText("Invalid YouTube URL");
            return;
        }

        lblStatus.setText("Loading video player...");
        loadYouTubePlayer(videoId);
    }

    @FXML
    private void onStop() {
        engine.loadContent("<html><body style='margin:0;background:black;'></body></html>");
        lblStatus.setText("Playback stopped");
    }

    private void loadYouTubePlayer(String videoId) {
        System.out.println("Loading video ID: " + videoId);

        String html = """
            <!DOCTYPE html>
            <html>
              <head>
                <style>
                  * { margin: 0; padding: 0; }
                  body { 
                    background: #000;
                    overflow: hidden;
                  }
                  #player-container {
                    position: relative;
                    width: 100vw;
                    height: 100vh;
                  }
                  iframe {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%%;
                    height: 100%%;
                    border: none;
                  }
                </style>
              </head>
              <body>
                <div id='player-container'>
                  <iframe 
                    id='ytplayer'
                    src='https://www.youtube.com/embed/%s?autoplay=1&controls=1&modestbranding=1&rel=0&enablejsapi=1'
                    allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture'
                    allowfullscreen>
                  </iframe>
                </div>
              </body>
            </html>
            """.formatted(videoId);

        engine.loadContent(html);
    }

    private String extractVideoId(String url) {
        try {
            if (url.contains("watch?v=")) {
                int startIndex = url.indexOf("v=") + 2;
                int endIndex = url.indexOf('&', startIndex);
                if (endIndex == -1) {
                    return url.substring(startIndex);
                }
                return url.substring(startIndex, endIndex);
            }

            if (url.contains("youtu.be/")) {
                int startIndex = url.lastIndexOf('/') + 1;
                int endIndex = url.indexOf('?', startIndex);
                if (endIndex == -1) {
                    return url.substring(startIndex);
                }
                return url.substring(startIndex, endIndex);
            }

            if (url.contains("embed/")) {
                int startIndex = url.indexOf("embed/") + 6;
                int endIndex = url.indexOf('?', startIndex);
                if (endIndex == -1) {
                    return url.substring(startIndex);
                }
                return url.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}