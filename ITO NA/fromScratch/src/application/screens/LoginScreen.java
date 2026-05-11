package application.screens;

import java.io.File;

import application.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.User;

public class LoginScreen {
    // Reference to the main app controller
    private AppController app;

    // Root layout of this screen
    private VBox root;

    // Constructor
    public LoginScreen(AppController app) {
        this.app = app;
        build();
    }

    // Builds the whole login screen
    private void build() {
        root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-screen");

        // Logo shown at the top of the login screen
        ImageView logoView = new ImageView();

        try {
            File logoFile = new File("images/logo.png");
            if (logoFile.exists()) {
                logoView.setImage(new Image(logoFile.toURI().toString()));
            } else {
                System.out.println("Logo not found: " + logoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Failed to load logo.");
            e.printStackTrace();
        }

        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

        // Main title of the system
        Label title = new Label("Wizards of Batong Malake");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        // Subtitle under the title
        Label subtitle = new Label("Retail Management System");
        subtitle.setStyle("-fx-text-fill: #f5d28a; -fx-font-size: 18px;");

        // Username input field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(280);
        usernameField.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: black;"
        );

        // Password input field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);
        passwordField.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: black;"
        );

        // Login button
        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(280);
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle(
                "-fx-background-color: #d6b36a;" +
                "-fx-text-fill: black;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        // Small note showing sample accounts
        Label note = new Label("Manager: manager / 1234     Cashier: cashier / 1234");
        note.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        // What happens when the login button is clicked
        loginBtn.setOnAction(e -> {
            User user = app.getRetailSystem().login(
                    usernameField.getText(),
                    passwordField.getText()
            );

            // Show error if login fails
            if (user == null) {
                app.showAlert("Login Failed", "Invalid username or password.");
                return;
            }

            // Save the logged-in user
            app.setCurrentUser(user);

            // Open dashboard depending on role
            if (user.getRole().equals("Manager")) {
                app.showManagerDashboard();
            } else {
                app.showCashierDashboard();
            }
        });

        // Add all components to the layout
        root.getChildren().addAll(
                logoView,
                title,
                subtitle,
                usernameField,
                passwordField,
                loginBtn,
                note
        );
    }

    // Returns the root layout so AppController can display this screen
    public VBox getRoot() {
        return root;
    }
}