package dk.easv.mytunes.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;

public class YouTubePlayerController {

    @FXML public TextField txtUrl;  // Made public so YouTubePlayer can access it
    @FXML private WebView webView;
    @FXML private Label lblStatus;

    private WebEngine engine;

    @FXML
    private void initialize() {
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                lblStatus.setText("Audio player loaded successfully");
            } else if (newState == Worker.State.FAILED) {
                lblStatus.setText("Failed to load player");
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

        lblStatus.setText("Loading audio player...");
        loadYouTubePlayer(videoId);
    }

    @FXML
    private void onStop() {
        engine.loadContent("<html><body style='margin:0;background:black;'></body></html>");
        lblStatus.setText("Playback stopped");
    }

    private void loadYouTubePlayer(String videoId) {
        String html = """
            <!DOCTYPE html>
            <html>
              <head>
                <style>
                  body { margin: 0; background: black; overflow: hidden; }
                  #player { position: absolute; width: 1px; height: 1px; }
                </style>
              </head>
              <body>
                <div id='player'></div>
                
                <script>
                  var tag = document.createElement('script');
                  tag.src = "https://www.youtube.com/iframe_api";
                  var firstScriptTag = document.getElementsByTagName('script')[0];
                  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                  var player;
                  function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                      height: '1',
                      width: '1',
                      videoId: '%s',
                      playerVars: {
                        'autoplay': 1,
                        'controls': 0,
                        'showinfo': 0,
                        'modestbranding': 1,
                        'rel': 0
                      },
                      events: {
                        'onReady': onPlayerReady,
                        'onStateChange': onPlayerStateChange
                      }
                    });
                  }

                  function onPlayerReady(event) {
                    event.target.playVideo();
                    event.target.unMute();
                  }

                  function onPlayerStateChange(event) {
                    if (event.data == 0) {
                      console.log('Video ended');
                    }
                  }
                </script>
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