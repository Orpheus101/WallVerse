package com.wallverse.ui;

import com.wallverse.dao.WallpaperActionDao;
import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class WallpaperCard {
    private final Node node;
    private final Wallpaper wallpaper;
    private final Runnable onClick;

    public WallpaperCard(Wallpaper wallpaper, Runnable onClick) {
        this.wallpaper = wallpaper;
        this.onClick = onClick;
        this.node = buildNode();
    }

    private Node buildNode() {
        ImageView imageView = new ImageView(new Image(wallpaper.getThumbUrl(), true));
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);

        Text label = new Text(wallpaper.getCategory() + " | " + wallpaper.getId());
        label.getStyleClass().add("card-title");

        VBox card = new VBox(6, imageView, label);
        card.getStyleClass().add("card");
        card.setOnMouseClicked(e -> onClick.run());

        return card;
    }

    public void addUnfavoriteButton(User user, WallpaperActionDao actionDao, Runnable onRemove) {
        VBox card = (VBox) node;
        Button unfavBtn = new Button("Remove Favorite");
        unfavBtn.getStyleClass().add("secondary-button");
        unfavBtn.setOnAction(e -> {
            actionDao.removeFavorite(user.getId(), wallpaper.getId());
            onRemove.run();
        });
        card.getChildren().add(unfavBtn);
    }

    public void addUnlikeButton(User user, WallpaperActionDao actionDao, Runnable onRemove) {
        VBox card = (VBox) node;
        Button unlikeBtn = new Button("Remove Like");
        unlikeBtn.getStyleClass().add("secondary-button");
        unlikeBtn.setOnAction(e -> {
            actionDao.removeLike(user.getId(), wallpaper.getId());
            onRemove.run();
        });
        card.getChildren().add(unlikeBtn);
    }

    public Node getNode() {
        return node;
    }

    public Wallpaper getWallpaper() {
        return wallpaper;
    }
}
