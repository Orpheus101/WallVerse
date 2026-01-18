package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.api.WallhavenClient;
import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import com.wallverse.util.CategoryData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javafx.scene.layout.StackPane;
import java.util.List;

public class MainView {
    private final MainApp app;
    private final User user;
    private final WallhavenClient client = new WallhavenClient();
    
    // stackRoot allows us to layer the overlay on top of the main layout
    private final StackPane stackRoot = new StackPane();
    private final BorderPane mainLayout = new BorderPane();
    
    private final TilePane tile = new TilePane();
    private final Text statusTitle = new Text();
    private final Text statusSubtitle = new Text();
    private final VBox statusBox = new VBox(4, statusTitle, statusSubtitle);

    public MainView(MainApp app, User user) {
        this.app = app;
        this.user = user;
        build();
        loadTrending();
    }

    private void build() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(12));
        topBar.getStyleClass().add("topbar");

        Button homeBtn = new Button("Home");
        Button favoritesBtn = new Button("Favorites");
        Button likedBtn = new Button("Liked");
        TextField searchField = new TextField();
        searchField.setPromptText("Search wallpapers (e.g., anime)");
        searchField.getStyleClass().add("input");
        searchField.setPrefWidth(360);

        ComboBox<String> categories = new ComboBox<>();
        for (String c : CategoryData.CATEGORIES) {
            categories.getItems().add(c);
        }
        categories.getSelectionModel().selectFirst();
        categories.getStyleClass().add("input");
        categories.setPrefWidth(140);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("primary-button");
        homeBtn.getStyleClass().add("ghost-button");
        favoritesBtn.getStyleClass().add("ghost-button");
        likedBtn.getStyleClass().add("ghost-button");

        Text welcome = new Text();
        welcome.getStyleClass().add("welcome");
        HBox authActions = new HBox(8);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        homeBtn.setOnAction(e -> loadTrending());
        favoritesBtn.setOnAction(e -> app.showFavorites(user));
        likedBtn.setOnAction(e -> app.showLiked(user));
        searchBtn.setOnAction(e -> searchWallpapers(searchField.getText(), categories.getValue()));

        if (user.isGuest()) {
            welcome.setText("Browsing as Guest");
            Button loginBtn = new Button("Sign In");
            Button signupBtn = new Button("Create Account");
            loginBtn.getStyleClass().add("ghost-button");
            signupBtn.getStyleClass().add("primary-button");
            loginBtn.setOnAction(e -> app.showLogin());
            signupBtn.setOnAction(e -> app.showSignup());
            authActions.getChildren().addAll(loginBtn, signupBtn);
        } else {
            welcome.setText("Welcome, " + user.getUsername());
        }

        topBar.getChildren().addAll(homeBtn, favoritesBtn, likedBtn, searchField, categories, searchBtn, spacer, welcome, authActions);

        tile.setPadding(new Insets(15));
        tile.setHgap(15);
        tile.setVgap(15);
        tile.getStyleClass().add("grid");

        ScrollPane scroll = new ScrollPane(tile);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll");

        statusTitle.getStyleClass().add("status-title");
        statusSubtitle.getStyleClass().add("status-subtitle");
        statusBox.getStyleClass().add("status-box");

        VBox content = new VBox(12, statusBox, scroll);
        content.setPadding(new Insets(10, 20, 20, 20));
        content.getStyleClass().add("content");

        mainLayout.setTop(topBar);
        mainLayout.setCenter(content);
        
        stackRoot.getChildren().add(mainLayout);
    }

    private void loadTrending() {
        // We run the network call in a new thread so the UI doesn't freeze
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setStatus("Loading trending wallpapers...", "Curated picks from Wallhaven.");
                    
                    // Fetch the wallpapers from the API
                    List<Wallpaper> list = client.getTrending();
                    
                    // Update the UI on the JavaFX thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showWallpapers(list);
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setStatus("Could not load wallpapers.", "Check your connection or API key.");
                            showAlert("Failed to load trending wallpapers.");
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void searchWallpapers(String keyword, String category) {
        // We run the search in a new thread so the UI doesn't freeze
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setStatus("Searching...", "Looking for matching wallpapers.");
                    
                    // Call the search method in our client
                    List<Wallpaper> list = client.search(keyword, category);
                    
                    // Update the UI on the JavaFX thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showWallpapers(list);
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setStatus("Search failed.", "Check your connection or API key.");
                            showAlert("Search failed. Check your connection.");
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void showWallpapers(List<Wallpaper> wallpapers) {
        tile.getChildren().clear();
        if (wallpapers.isEmpty()) {
            setStatus("No results found.", "Try a different keyword or category.");
            return;
        }
        setStatus("Browse wallpapers", wallpapers.size() + " results loaded.");

        for (Wallpaper w : wallpapers) {
            Image image = new Image(w.getThumbUrl(), 250, 0, true, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);

            Text label = new Text(w.getCategory() + " | " + w.getId());
            label.getStyleClass().add("card-title");
            VBox card = new VBox(6, imageView, label);
            card.getStyleClass().add("card");

            card.setOnMouseClicked(e -> openOverlay(w));
            tile.getChildren().add(card);
        }
    }

    /**
     * Opens the Pinterest-style details overlay on top of the current view.
     */
    public void openOverlay(Wallpaper w) {
        // Create the overlay and tell it about its parent (stackRoot)
        DetailsOverlay overlay = new DetailsOverlay(app, user, w, stackRoot);
        
        // Add it to the top of our stack
        stackRoot.getChildren().add(overlay);
        
        // Important for capturing keyboard events like ESC
        overlay.requestFocus();
    }

    public Parent getView() {
        return stackRoot;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("WallVerse");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setStatus(String title, String subtitle) {
        Platform.runLater(() -> {
            statusTitle.setText(title);
            statusSubtitle.setText(subtitle);
        });
    }
}
