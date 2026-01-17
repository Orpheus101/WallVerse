package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.dao.WallpaperActionDao;
import com.wallverse.db.DbUtil;
import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import com.wallverse.util.DownloadUtil;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DetailView {
    private final MainApp app;
    private final User user;
    private final Wallpaper wallpaper;
    private final WallpaperActionDao actionDao = new WallpaperActionDao();
    private final BorderPane root = new BorderPane();

    public DetailView(MainApp app, User user, Wallpaper wallpaper) {
        this.app = app;
        this.user = user;
        this.wallpaper = wallpaper;
        build();
    }

    private void build() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("detail-content");

        Image image = new Image(wallpaper.getUrl(), 900, 0, true, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("detail-image");

        Text tags = new Text("Tags: " + wallpaper.getTags());
        Text category = new Text("Category: " + wallpaper.getCategory());
        tags.getStyleClass().add("detail-meta");
        category.getStyleClass().add("detail-meta");

        boolean actionsEnabled = user != null && !user.isGuest() && DbUtil.isAvailable();
        String likeLabel = actionsEnabled && actionDao.isLiked(user.getId(), wallpaper.getId()) ? "Liked" : "Like";
        String favLabel = actionsEnabled && actionDao.isFavorited(user.getId(), wallpaper.getId()) ? "Favorited" : "Add to Favorites";

        Button likeBtn = new Button(likeLabel);
        Button favBtn = new Button(favLabel);
        Button downloadBtn = new Button("Download");
        Button backBtn = new Button("Back");
        likeBtn.getStyleClass().add("primary-button");
        favBtn.getStyleClass().add("ghost-button");
        downloadBtn.getStyleClass().add("ghost-button");
        backBtn.getStyleClass().add("ghost-button");

        if (!actionsEnabled) {
            likeBtn.setText("Login to Like");
            favBtn.setText("Login for Favorites");
            likeBtn.setDisable(true);
            favBtn.setDisable(true);
        }

        likeBtn.setOnAction(e -> {
            boolean ok = actionDao.addLike(user.getId(), wallpaper);
            if (ok) {
                likeBtn.setText("Liked");
                showAlert("Liked!");
            } else {
                showAlert("Like failed.");
            }
        });

        favBtn.setOnAction(e -> {
            boolean ok = actionDao.addFavorite(user.getId(), wallpaper);
            if (ok) {
                favBtn.setText("Favorited");
                showAlert("Added to favorites.");
            } else {
                showAlert("Favorite failed.");
            }
        });

        downloadBtn.setOnAction(e -> {
            String name = wallpaper.getId() + ".jpg";
            boolean ok = DownloadUtil.downloadImage(wallpaper.getUrl(), name);
            if (ok) {
                showAlert("Downloaded to ~/Downloads/" + name);
            } else {
                showAlert("Download failed.");
            }
        });

        backBtn.setOnAction(e -> app.showMain());

        HBox actions = new HBox(10, likeBtn, favBtn, downloadBtn, backBtn);
        actions.getStyleClass().add("detail-actions");
        content.getChildren().addAll(imageView, category, tags, actions);

        root.setCenter(content);
    }

    public Parent getView() {
        return root;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Wallpaper");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
