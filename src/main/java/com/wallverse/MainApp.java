package com.wallverse;

import com.wallverse.model.User;
import com.wallverse.model.Wallpaper;
import com.wallverse.ui.DetailView;
import com.wallverse.ui.FavoritesView;
import com.wallverse.ui.LikedView;
import com.wallverse.ui.LoginView;
import com.wallverse.ui.MainView;
import com.wallverse.ui.SignupView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage stage;
    private User currentUser;
    private MainView currentMainView;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("WallVerse");
        showLogin();
        this.stage.show();
    }

    public void showLogin() {
        LoginView view = new LoginView(this);
        Scene scene = new Scene(view.getView(), 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showSignup() {
        SignupView view = new SignupView(this);
        Scene scene = new Scene(view.getView(), 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showMain() {
        MainView view = new MainView(this, currentUser);
        currentMainView = view; // Store reference for later use
        Scene scene = new Scene(view.getView(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showDetail(Wallpaper wallpaper) {
        // For backwards compatibility, we'll just open the overlay in the main view
        // But since DetailView doesn't exist anymore, we'll just show an error
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid Operation");
        alert.setContentText("Direct detail view is no longer supported. Use the main view.");
        alert.showAndWait();
    }

    public void showFavorites(User user) {
        FavoritesView view = new FavoritesView(this, user);
        Scene scene = new Scene(view.getView(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showLiked(User user) {
        LikedView view = new LikedView(this, user);
        Scene scene = new Scene(view.getView(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void openOverlay(Wallpaper wallpaper) {
        if (currentMainView != null) {
            currentMainView.openOverlay(wallpaper);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
