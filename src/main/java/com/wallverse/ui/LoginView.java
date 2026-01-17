package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.dao.UserDao;
import com.wallverse.db.DbUtil;
import com.wallverse.model.User;
import com.wallverse.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginView {
    private final MainApp app;
    private final BorderPane root = new BorderPane();
    private final VBox panel = new VBox(12);

    public LoginView(MainApp app) {
        this.app = app;
        build();
    }

    private void build() {
        panel.setPadding(new Insets(30));
        panel.getStyleClass().addAll("panel", "auth-panel");
        panel.setFillWidth(true);
        panel.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("WallVerse");
        title.getStyleClass().add("title");

        Text subtitle = new Text("Curated wallpapers, built for focus.");
        subtitle.getStyleClass().add("subtitle");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Create Account");
        Button guestBtn = new Button("Continue as Guest");
        loginBtn.getStyleClass().add("primary-button");
        signupBtn.getStyleClass().add("ghost-button");
        guestBtn.getStyleClass().add("ghost-button");

        loginBtn.setOnAction(e -> {
            if (!DbUtil.isAvailable()) {
                showAlert("Database not available. Use Guest mode or configure MySQL.");
                return;
            }
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            String error = ValidationUtil.validateLogin(username, password);
            if (error != null) {
                showAlert(error);
                return;
            }

            UserDao userDao = new UserDao();
            User user = userDao.login(username, password);
            if (user != null) {
                app.setCurrentUser(user);
                app.showMain();
            } else {
                showAlert("Invalid username or password.");
            }
        });

        signupBtn.setOnAction(e -> app.showSignup());
        guestBtn.setOnAction(e -> {
            User guest = new User(0, "Guest", "", "", "");
            app.setCurrentUser(guest);
            app.showMain();
        });

        panel.getChildren().addAll(title, subtitle, usernameField, passwordField, loginBtn, signupBtn, guestBtn);
        root.setCenter(panel);
        BorderPane.setAlignment(panel, Pos.CENTER);
        BorderPane.setMargin(panel, new Insets(80, 0, 0, 0));
    }

    public Parent getView() {
        return root;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Login");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
