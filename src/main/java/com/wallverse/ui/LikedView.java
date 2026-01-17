package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.dao.WallpaperActionDao;
import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class LikedView {
    private final MainApp app;
    private final User user;
    private final WallpaperActionDao actionDao = new WallpaperActionDao();
    private final BorderPane root = new BorderPane();
    private final TilePane tile = new TilePane();
    private final Label statusLabel = new Label();

    public LikedView(MainApp app, User user) {
        this.app = app;
        this.user = user;
        build();
        loadLiked();
    }

    private void build() {
        // Top bar with back button
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(12));
        topBar.getStyleClass().add("topbar");

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("ghost-button");
        backBtn.setOnAction(e -> app.showMain());

        topBar.getChildren().add(backBtn);

        // Status label
        statusLabel.getStyleClass().add("status-title");
        VBox.setVgrow(tile, javafx.scene.layout.Priority.ALWAYS);

        VBox content = new VBox(12, statusLabel, tile);
        content.setPadding(new Insets(10, 20, 20, 20));
        content.getStyleClass().add("content");

        root.setTop(topBar);
        root.setCenter(content);
    }

    private void loadLiked() {
        if (user.isGuest()) {
            statusLabel.setText("Please log in to see your liked wallpapers.");
            return;
        }

        List<Wallpaper> liked = actionDao.getLikedWallpapers(user.getId());
        showWallpapers(liked);
    }

    private void showWallpapers(List<Wallpaper> wallpapers) {
        tile.getChildren().clear();

        if (wallpapers.isEmpty()) {
            statusLabel.setText("No liked wallpapers yet.");
            return;
        }

        statusLabel.setText("Your liked wallpapers (" + wallpapers.size() + ")");

        for (Wallpaper w : wallpapers) {
            WallpaperCard card = new WallpaperCard(w, () -> app.openOverlay(w));
            // Add unlike button to the card
            card.addUnlikeButton(user, actionDao, () -> {
                // Remove from the UI when unliked
                tile.getChildren().remove(card.getNode());
                if (tile.getChildren().isEmpty()) {
                    statusLabel.setText("No liked wallpapers yet.");
                }
            });
            tile.getChildren().add(card.getNode());
        }
    }

    public Parent getView() {
        return root;
    }
}
