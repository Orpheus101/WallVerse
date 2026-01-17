package com.wallverse.ui;

import com.wallverse.MainApp;
import com.wallverse.dao.UserDao;
import com.wallverse.db.DbUtil;
import com.wallverse.model.User;
import com.wallverse.util.PasswordUtil;
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

public class SignupView {
    private final MainApp app;
    private final BorderPane root = new BorderPane();
    private final VBox panel = new VBox(12);

    public SignupView(MainApp app) {
        this.app = app;
        build();
    }

    private void build() {
        panel.setPadding(new Insets(30));
        panel.getStyleClass().addAll("panel", "auth-panel");
        panel.setFillWidth(true);
        panel.setAlignment(Pos.CENTER_LEFT);

        Text title = new Text("Create Account");
        title.getStyleClass().add("title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");

        Button signupBtn = new Button("Sign Up");
        Button backBtn = new Button("Back to Login");
        signupBtn.getStyleClass().add("primary-button");
        backBtn.getStyleClass().add("ghost-button");

        signupBtn.setOnAction(e -> {
            if (!DbUtil.isAvailable()) {
                showAlert("Database not available. Configure MySQL to create accounts.");
                return;
            }
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            String error = ValidationUtil.validateSignup(username, email, password);
            if (error != null) {
                showAlert(error);
                return;
            }

            UserDao userDao = new UserDao();
            if (userDao.usernameExists(username) || userDao.emailExists(email)) {
                showAlert("Username or email already exists.");
                return;
            }

            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(password, salt);

            User user = new User(0, username, email, hash, salt);
            boolean created = userDao.createUser(user);

            if (created) {
                showAlert("Account created. Please log in.");
                app.showLogin();
            } else {
                showAlert("Signup failed. Try again.");
            }
        });

        backBtn.setOnAction(e -> app.showLogin());

        panel.getChildren().addAll(title, usernameField, emailField, passwordField, signupBtn, backBtn);
        root.setCenter(panel);
        BorderPane.setAlignment(panel, Pos.CENTER);
        BorderPane.setMargin(panel, new Insets(80, 0, 0, 0));
    }

    public Parent getView() {
        return root;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Signup");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
