package application.screens;

import java.io.File;

import application.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DashboardScreen {
    // Reference to the main app controller
    private AppController app;

    // Checks if the current dashboard is for manager or cashier
    private boolean isManager;

    // Root layout of this screen
    private VBox root;

    // Constructor
    public DashboardScreen(AppController app, boolean isManager) {
        this.app = app;
        this.isManager = isManager;
        build();
    }

    // Builds the whole dashboard screen
    private void build() {
        root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("root-screen");

        // Logo at the top of the dashboard
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

        logoView.setFitWidth(90);
        logoView.setFitHeight(90);
        logoView.setPreserveRatio(true);

        // Dashboard title changes depending on user role
        Label title = new Label(isManager ? "Manager Dashboard" : "Cashier Dashboard");
        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;"
        );

        // Small subtitle under the title
        Label subtitle = new Label("Choose a module to manage the potion shop.");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b5a00;");

        // Main dashboard buttons
        Button salesBtn = createDashboardButton(
                "Sales Management",
                "images/Sales_Management.png"
        );

        Button inventoryBtn = createDashboardButton(
                isManager ? "Inventory Management" : "View Inventory",
                "images/Inventory_Management.png"
        );

        Button menuBtn = createDashboardButton(
                "Menu Management",
                "images/Menu_Management.png"
        );

        Button cashBtn = createDashboardButton(
                "Cash Transaction Management",
                "images/Cash_Management.png"
        );

        Button logoutBtn = createDashboardButton(
                "Logout",
                "images/Logout.png"
        );

        // What happens when each button is clicked
        salesBtn.setOnAction(e -> app.showSalesScreen());
        inventoryBtn.setOnAction(e -> app.showInventoryScreen());
        menuBtn.setOnAction(e -> app.showMenuScreen());
        cashBtn.setOnAction(e -> app.showCashTransactionScreen());
        logoutBtn.setOnAction(e -> app.showLoginScreen());

        // Layout for arranging dashboard buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);

        // First row
        buttonGrid.add(salesBtn, 0, 0);
        buttonGrid.add(inventoryBtn, 1, 0);

        // Second and third row depending on role
        if (isManager) {
            buttonGrid.add(menuBtn, 0, 1);
            buttonGrid.add(cashBtn, 1, 1);
            buttonGrid.add(logoutBtn, 0, 2, 2, 1);
        } else {
            buttonGrid.add(cashBtn, 0, 1);
            buttonGrid.add(logoutBtn, 1, 1);
        }

        // Add everything to the root layout
        root.getChildren().addAll(logoView, title, subtitle, buttonGrid);
    }

    // Creates one dashboard button with text and image
    private Button createDashboardButton(String text, String imagePath) {
        Button button = new Button(text);
        button.setPrefWidth(220);
        button.setPrefHeight(140);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;" +
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 18;" +
                "-fx-cursor: hand;"
        );

        // Load the button icon
        try {
            File file = new File(imagePath);

            if (file.exists()) {
                ImageView icon = new ImageView(new Image(file.toURI().toString()));
                icon.setFitWidth(42);
                icon.setFitHeight(42);
                icon.setPreserveRatio(true);
                button.setGraphic(icon);
            } else {
                System.out.println("Button icon not found: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Failed to load button icon: " + imagePath);
            e.printStackTrace();
        }

        // Hover effect when mouse enters the button
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;" +
                "-fx-background-color: #fff3cd;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #ffb703;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 18;" +
                "-fx-cursor: hand;"
        ));

        // Restore original style when mouse leaves the button
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;" +
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 18;" +
                "-fx-cursor: hand;"
        ));

        return button;
    }

    // Returns the root layout so AppController can display this screen
    public VBox getRoot() {
        return root;
    }
}