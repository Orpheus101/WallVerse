package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.api.WallhavenClient;
import com.wallverse.dao.WallpaperActionDao;
import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import com.wallverse.util.DownloadUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class DetailsOverlay extends StackPane {
    private final MainApp app;
    private final User user;
    private Wallpaper wallpaper;
    private final WallhavenClient client = new WallhavenClient();
    private final WallpaperActionDao actionDao = new WallpaperActionDao();

    private final VBox detailsCard = new VBox();
    private final StackPane backgroundPane = new StackPane();
    private final StackPane parent;

    public DetailsOverlay(MainApp app, User user, Wallpaper wallpaper, StackPane parent) {
        this.app = app;
        this.user = user;
        this.wallpaper = wallpaper;
        this.parent = parent;

        build();
        loadDetails();
    }

    private void build() {
        // 1. Dimmed Background
        backgroundPane.getStyleClass().add("overlay-background");
        backgroundPane.setOnMouseClicked(e -> close());

        // 2. Details Card
        detailsCard.getStyleClass().add("details-card");
        detailsCard.setMaxSize(1000, 700);
        detailsCard.setAlignment(Pos.CENTER);
        
        // Prevent clicks inside card from closing overlay
        detailsCard.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> e.consume());

        // Header with Back Button
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("ghost-button");
        backBtn.setOnAction(e -> close());
        header.getChildren().add(backBtn);

        // Content: Left (Image), Right (Info)
        HBox content = new HBox(30);
        content.setPadding(new Insets(0, 30, 30, 30));
        VBox.setVgrow(content, Priority.ALWAYS);
        content.setAlignment(Pos.CENTER);

        // Responsive behavior: if window is narrow, stack vertically
        this.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 900) {
                if (content.getChildren().size() > 0 && !(detailsCard.getChildren().get(1) instanceof VBox)) {
                    // This is a bit complex for basics, so we'll stick to a clean HBox for now 
                    // but ensure the card itself scales.
                }
            }
        });

        // Left: Preview Image
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("image-preview-container");
        imageContainer.setPrefWidth(600);
        
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        imageView.setFitHeight(550);
        
        // Use thumbnail first for instant preview
        Image thumb = new Image(wallpaper.getThumbUrl(), true);
        imageView.setImage(thumb);
        
        imageContainer.getChildren().add(imageView);

        // Right: Metadata
        VBox infoPanel = new VBox(20);
        infoPanel.setPrefWidth(350);
        infoPanel.getStyleClass().add("info-panel");

        Text title = new Text("Loading wallpaper details...");
        title.getStyleClass().add("detail-title");
        title.setWrappingWidth(320);

        VBox metaList = new VBox(12);
        metaList.getChildren().addAll(
            createMetaRow("Uploader", "..."),
            createMetaRow("Resolution", wallpaper.getResolution()),
            createMetaRow("Views", "..."),
            createMetaRow("Category", wallpaper.getCategory())
        );

        // Actions
        VBox actions = new VBox(12);
        actions.setPadding(new Insets(20, 0, 0, 0));
        
        Button likeBtn = new Button("Like");
        Button favBtn = new Button("Add to Favorites");
        Button downloadBtn = new Button("Download Image");
        
        likeBtn.setMaxWidth(Double.MAX_VALUE);
        favBtn.setMaxWidth(Double.MAX_VALUE);
        downloadBtn.setMaxWidth(Double.MAX_VALUE);
        
        likeBtn.getStyleClass().add("primary-button");
        favBtn.getStyleClass().add("secondary-button");
        downloadBtn.getStyleClass().add("secondary-button");

        // Action Handlers
        likeBtn.setOnAction(e -> handleLike(likeBtn));
        favBtn.setOnAction(e -> handleFavorite(favBtn));
        downloadBtn.setOnAction(e -> handleDownload(downloadBtn));

        actions.getChildren().addAll(likeBtn, favBtn, downloadBtn);
        
        ScrollPane scrollInfo = new ScrollPane(infoPanel);
        scrollInfo.setFitToWidth(true);
        scrollInfo.getStyleClass().add("scroll");

        infoPanel.getChildren().addAll(title, metaList, actions);
        content.getChildren().addAll(imageContainer, scrollInfo);

        detailsCard.getChildren().addAll(header, content);

        this.getChildren().addAll(backgroundPane, detailsCard);
        this.setAlignment(Pos.CENTER);

        // Animations
        animateIn();

        // Keyboard handler
        this.setFocusTraversable(true);
        this.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                close();
                e.consume();
            }
        });
    }

    private void loadDetails() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Fetch full details
                    Wallpaper details = client.fetchWallpaperDetailsById(wallpaper.getId());
                    wallpaper = details;
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void updateUI() {
        // Find elements in the tree
        HBox content = (HBox) detailsCard.getChildren().get(1);
        ScrollPane scrollInfo = (ScrollPane) content.getChildren().get(1);
        VBox infoPanel = (VBox) scrollInfo.getContent();
        
        Text titleText = (Text) infoPanel.getChildren().get(0);
        titleText.setText(wallpaper.getDisplayTitle());

        VBox metaList = (VBox) infoPanel.getChildren().get(1);
        metaList.getChildren().clear();
        metaList.getChildren().addAll(
            createMetaRow("Uploader", wallpaper.getUploader()),
            createMetaRow("Resolution", wallpaper.getResolution()),
            createMetaRow("Views", String.valueOf(wallpaper.getViews())),
            createMetaRow("Favorites", String.valueOf(wallpaper.getFavorites())),
            createMetaRow("Category", wallpaper.getCategory()),
            createMetaRow("Type", wallpaper.getFileType().toUpperCase())
        );

        // Update image to full resolution
        StackPane imageContainer = (StackPane) content.getChildren().get(0);
        ImageView imageView = (ImageView) imageContainer.getChildren().get(0);
        Image fullImage = new Image(wallpaper.getUrl(), true);
        imageView.setImage(fullImage);
    }

    private HBox createMetaRow(String label, String value) {
        Text l = new Text(label + ": ");
        l.getStyleClass().add("meta-label");
        Text v = new Text(value);
        v.getStyleClass().add("meta-value");
        return new HBox(l, v);
    }

    private void handleLike(Button btn) {
        if (user.isGuest()) {
            return;
        }
        boolean ok = actionDao.addLike(user.getId(), wallpaper);
        if (ok) {
            btn.setText("Liked!");
            btn.setDisable(true);
        }
    }

    private void handleFavorite(Button btn) {
        if (user.isGuest()) {
            return;
        }
        boolean ok = actionDao.addFavorite(user.getId(), wallpaper);
        if (ok) {
            btn.setText("Favorited!");
            btn.setDisable(true);
        }
    }

    private void handleDownload(Button btn) {
        btn.setText("Downloading...");
        btn.setDisable(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Make sure we have the direct image URL (path field) before downloading
                String imageUrl = wallpaper.getUrl(); // This is now the direct image URL after loadDetails()
                String fileName = "wallhaven-" + wallpaper.getId() + "." + wallpaper.getFileType();
                
                boolean ok = DownloadUtil.downloadImage(imageUrl, fileName);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (ok) {
                            btn.setText("Downloaded!");
                        } else {
                            btn.setText("Failed");
                            btn.setDisable(false);
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private void animateIn() {
        FadeTransition ftBackground = new FadeTransition(Duration.millis(200), backgroundPane);
        ftBackground.setFromValue(0);
        ftBackground.setToValue(1);

        ScaleTransition stCard = new ScaleTransition(Duration.millis(220), detailsCard);
        stCard.setFromX(0.98);
        stCard.setFromY(0.98);
        stCard.setToX(1.0);
        stCard.setToY(1.0);

        FadeTransition ftCard = new FadeTransition(Duration.millis(220), detailsCard);
        ftCard.setFromValue(0);
        ftCard.setToValue(1);

        ParallelTransition pt = new ParallelTransition(ftBackground, stCard, ftCard);
        pt.play();
    }

    private void close() {
        FadeTransition ft = new FadeTransition(Duration.millis(150), this);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            // Remove ourself from the parent stack
            parent.getChildren().remove(this);
        });
        ft.play();
    }
}
