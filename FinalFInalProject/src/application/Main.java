package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import model.InventoryItem;
import model.InventoryUsage;
import model.MenuItem;
import model.User;
import system.RetailSystem;

public class Main extends Application {

    private Stage stage;
    private RetailSystem retailSystem;
    private User currentUser;

    private final int WIDTH = 1000;
    private final int HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        retailSystem = new RetailSystem();
        retailSystem.initializeDefaultData();

        stage.setTitle("Wizards of Batong Malake");
        showLoginScreen();
        stage.show();
    }

    private void showLoginScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b1d0e;");

        Label title = new Label("Wizards of Batong Malake");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold;");

        Label subtitle = new Label("Retail Management System");
        subtitle.setStyle("-fx-text-fill: #f5d28a; -fx-font-size: 18px;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(250);

        Label note = new Label("Manager: manager / 1234     Cashier: cashier / 1234");
        note.setStyle("-fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            currentUser = retailSystem.login(usernameField.getText(), passwordField.getText());

            if (currentUser == null) {
                showAlert("Login Failed", "Invalid username or password.");
            } else if (currentUser.getRole().equals("Manager")) {
                showManagerDashboard();
            } else {
                showCashierDashboard();
            }
        });

        root.getChildren().addAll(title, subtitle, usernameField, passwordField, loginBtn, note);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
    }

    private void showManagerDashboard() {
        VBox root = baseLayout("Manager Dashboard");

        Button inventoryBtn = new Button("Inventory Management");
        Button menuBtn = new Button("Menu Management");
        Button logoutBtn = new Button("Logout");

        inventoryBtn.setPrefWidth(250);
        menuBtn.setPrefWidth(250);
        logoutBtn.setPrefWidth(250);

        inventoryBtn.setOnAction(e -> showInventoryScreen());
        menuBtn.setOnAction(e -> showMenuScreen());
        logoutBtn.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(inventoryBtn, menuBtn, logoutBtn);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
    }

    private void showCashierDashboard() {
        VBox root = baseLayout("Cashier Dashboard");

        Button inventoryBtn = new Button("View Inventory");
        Button menuBtn = new Button("View Menu");
        Button logoutBtn = new Button("Logout");

        inventoryBtn.setPrefWidth(250);
        menuBtn.setPrefWidth(250);
        logoutBtn.setPrefWidth(250);

        inventoryBtn.setOnAction(e -> showInventoryScreen());
        menuBtn.setOnAction(e -> showMenuScreen());
        logoutBtn.setOnAction(e -> showLoginScreen());

        root.getChildren().addAll(inventoryBtn, menuBtn, logoutBtn);
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
    }

    private void showInventoryScreen() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Inventory Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        TableView<InventoryItem> table = new TableView<>();

        TableColumn<InventoryItem, String> nameCol = new TableColumn<>("Ingredient");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<InventoryItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<InventoryItem, Integer> stockCol = new TableColumn<>("Stock Level");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stocks"));

        TableColumn<InventoryItem, Double> costCol = new TableColumn<>("Cost Per Unit");
        costCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<InventoryItem, Integer> thresholdCol = new TableColumn<>("Low Stock Limit");
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("lowStockThreshold"));

        table.getColumns().addAll(nameCol, descCol, stockCol, costCol, thresholdCol);
        table.setItems(FXCollections.observableArrayList(retailSystem.getInventory().getItems()));

        TextArea usageArea = new TextArea();
        usageArea.setEditable(false);
        usageArea.setPrefHeight(180);
        usageArea.setPromptText("Select an ingredient to view where it is used.");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedItem) -> {
            if (selectedItem != null) {
                usageArea.setText(getIngredientUsageReport(selectedItem));
            }
        });

        Button lowStockBtn = new Button("Show Low Stock Alerts");
        lowStockBtn.setMaxWidth(Double.MAX_VALUE);

        lowStockBtn.setOnAction(e -> {
            StringBuilder message = new StringBuilder();

            for (InventoryItem item : retailSystem.getInventory().getItems()) {
                if (item.isLowStock()) {
                    message.append(item.getItemName())
                           .append(" is low on stock. Current stock: ")
                           .append(item.getStocks())
                           .append("\n");
                }
            }

            if (message.length() == 0) {
                message.append("No low stock ingredients.");
            }

            showAlert("Low Stock Alert", message.toString());
        });

        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> goBackToDashboard());

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(320);
        rightPanel.getChildren().addAll(
                new Label("Ingredient Usage Monitor"),
                usageArea,
                lowStockBtn,
                backBtn
        );

        root.setTop(title);
        root.setCenter(table);
        root.setRight(rightPanel);

        stage.setScene(new Scene(root, WIDTH, HEIGHT));
    }

    private void showMenuScreen() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Menu Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        TableView<MenuItem> table = new TableView<>();

        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Menu Item");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<MenuItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<MenuItem, Double> costCol = new TableColumn<>("Ingredient Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("ingredientCost"));

        TableColumn<MenuItem, Double> profitCol = new TableColumn<>("Profit %");
        profitCol.setCellValueFactory(new PropertyValueFactory<>("profitPercentage"));

        TableColumn<MenuItem, Double> priceCol = new TableColumn<>("Selling Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MenuItem, String> ingredientsCol = new TableColumn<>("Ingredients Used");
        ingredientsCol.setCellValueFactory(new PropertyValueFactory<>("ingredientsSummary"));

        table.getColumns().addAll(nameCol, descCol, costCol, profitCol, priceCol, ingredientsCol);
        table.setItems(FXCollections.observableArrayList(retailSystem.getMenu().getItems()));

        TextField profitField = new TextField();
        profitField.setPromptText("New profit percentage");

        Button updateProfitBtn = new Button("Update Profit % and Recompute Price");
        updateProfitBtn.setMaxWidth(Double.MAX_VALUE);

        updateProfitBtn.setOnAction(e -> {
            MenuItem selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("No Menu Item Selected", "Please select a menu item first.");
                return;
            }

            try {
                double newProfit = Double.parseDouble(profitField.getText());
                selected.setProfitPercentage(newProfit);
                table.refresh();
                profitField.clear();

                showAlert(
                        "Price Updated",
                        selected.getItemName() + " now has a selling price of ₱" + selected.getPrice()
                );

            } catch (Exception ex) {
                showAlert("Invalid Input", "Please enter a valid profit percentage.");
            }
        });

        Button recomputeAllBtn = new Button("Recompute All Prices");
        recomputeAllBtn.setMaxWidth(Double.MAX_VALUE);

        recomputeAllBtn.setOnAction(e -> {
            for (MenuItem item : retailSystem.getMenu().getItems()) {
                item.updatePriceBasedOnIngredients();
            }

            table.refresh();
            showAlert("Prices Updated", "All menu prices were recomputed based on ingredient costs.");
        });

        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> goBackToDashboard());

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(320);
        rightPanel.getChildren().addAll(
                new Label("Price and Profit Controls"),
                profitField,
                updateProfitBtn,
                recomputeAllBtn,
                backBtn
        );

        root.setTop(title);
        root.setCenter(table);
        root.setRight(rightPanel);

        stage.setScene(new Scene(root, WIDTH, HEIGHT));
    }

    private String getIngredientUsageReport(InventoryItem selectedIngredient) {
        StringBuilder report = new StringBuilder();

        report.append(selectedIngredient.getItemName())
              .append(" is used in:\n\n");

        boolean found = false;

        for (MenuItem menuItem : retailSystem.getMenu().getItems()) {
            for (InventoryUsage usage : menuItem.getIngredients()) {
                if (usage.getInventoryItem() == selectedIngredient) {
                    report.append("- ")
                          .append(menuItem.getItemName())
                          .append(" uses ")
                          .append(usage.getQuantityNeeded())
                          .append(" unit/s\n");

                    found = true;
                }
            }
        }

        if (!found) {
            report.append("This ingredient is not currently used in any menu item.");
        }

        return report.toString();
    }

    private void goBackToDashboard() {
        if (currentUser.getRole().equals("Manager")) {
            showManagerDashboard();
        } else {
            showCashierDashboard();
        }
    }

    private VBox baseLayout(String heading) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4ead7;");

        Label title = new Label(heading);
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        root.getChildren().add(title);
        return root;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}