package application.screens;

import java.io.File;

import application.AppController;
import application.ui.ScreenHeader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import model.InventoryItem;

public class InventoryScreen {
    // Reference to the main app controller
    private AppController app;

    // Root layout of this screen
    private BorderPane root;

    // The currently selected inventory item
    private InventoryItem selectedInventoryItem;

    // The currently selected card in the inventory grid
    private VBox selectedInventoryCard;

    // Text area that shows the details of the selected ingredient
    private TextArea inventoryInfoArea;

    // Constructor
    public InventoryScreen(AppController app) {
        this.app = app;
        build();
    }

    // Builds the whole inventory screen
    private void build() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-screen");

        // Header with logo and title
        root.setTop(ScreenHeader.createHeader("Inventory Management"));

        // Grid for showing all ingredient cards
        TilePane ingredientGrid = new TilePane();
        ingredientGrid.setPadding(new Insets(20));
        ingredientGrid.setHgap(20);
        ingredientGrid.setVgap(20);
        ingredientGrid.setPrefColumns(4);
        ingredientGrid.setStyle("-fx-background-color: transparent;");

        // Add one card for each inventory item
        for (InventoryItem item : app.getRetailSystem().getInventory().getItems()) {
            ingredientGrid.getChildren().add(createIngredientCard(item));
        }

        // Scroll pane for the card grid
        ScrollPane scrollPane = new ScrollPane(ingredientGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;"
        );

        // Makes the viewport transparent too
        scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    if (scrollPane.lookup(".viewport") != null) {
                        scrollPane.lookup(".viewport").setStyle("-fx-background-color: transparent;");
                    }
                });
            }
        });

        // Field for entering restock amount
        TextField restockField = new TextField();
        restockField.setPromptText("Restock Amount");

        // Area for showing ingredient details
        inventoryInfoArea = new TextArea();
        inventoryInfoArea.setEditable(false);
        inventoryInfoArea.setWrapText(true);
        inventoryInfoArea.setPrefHeight(220);
        inventoryInfoArea.setPromptText("Click an ingredient to view details.");
        inventoryInfoArea.setStyle("-fx-text-fill: black;");

        // Restock button
        Button restockBtn = new Button("Restock Selected Ingredient");
        restockBtn.setMaxWidth(Double.MAX_VALUE);

        // Only manager can restock
        restockBtn.setDisable(!app.getCurrentUser().getRole().equals("Manager"));

        restockBtn.setOnAction(e -> {
            // Make sure a card is selected first
            if (selectedInventoryItem == null) {
                app.showAlert("No Ingredient Selected", "Click an ingredient card first.");
                return;
            }

            try {
                int amount = Integer.parseInt(restockField.getText());

                // Restock must be greater than 0
                if (amount <= 0) {
                    app.showAlert("Invalid Amount", "Restock amount must be greater than 0.");
                    return;
                }

                // Restock item and record it in cash management
                boolean success = app.getRetailSystem().restockInventoryItem(selectedInventoryItem, amount);

                if (!success) {
                    app.showAlert(
                            "Restock Failed",
                            "Not enough cash balance to restock this item."
                    );
                    return;
                }

                app.showAlert(
                        "Restock Successful",
                        selectedInventoryItem.getItemName()
                                + " stock increased by " + amount
                                + "\nTotal Cost: ₱" + String.format("%.2f", selectedInventoryItem.getPrice() * amount)
                );

                // Refresh screen after successful restock
                app.showInventoryScreen();

            } catch (Exception ex) {
                app.showAlert("Invalid Input", "Please enter a valid restock amount.");
            }
        });

        // Button to show all low-stock ingredients
        Button lowStockBtn = new Button("Show Low Stock Alerts");
        lowStockBtn.setMaxWidth(Double.MAX_VALUE);

        lowStockBtn.setOnAction(e -> {
            StringBuilder message = new StringBuilder();

            for (InventoryItem item : app.getRetailSystem().getInventory().getItems()) {
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

            app.showAlert("Low Stock Alert", message.toString());
        });

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> app.goBackToDashboard());

        // Labels on the right panel
        Label detailsLabel = new Label("Ingredient Details");
        detailsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label controlsLabel = new Label("Inventory Controls");
        controlsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Right side panel for details and controls
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(300);
        rightPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 15;"
        );

        rightPanel.getChildren().addAll(
                detailsLabel,
                inventoryInfoArea,
                controlsLabel,
                restockField,
                restockBtn,
                lowStockBtn,
                backBtn
        );

        // Put the card grid in the center and controls on the right
        root.setCenter(scrollPane);
        root.setRight(rightPanel);
    }

    // Creates one ingredient card
    private VBox createIngredientCard(InventoryItem item) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(16, 16, 16, 16));
        card.setPrefWidth(250);
        card.setMinWidth(250);
        card.setMaxWidth(250);
        card.setPrefHeight(430);
        card.setMinHeight(430);
        card.setMaxHeight(430);

        // Apply custom card background if the file exists
        File bgFile = new File("images/card_bg.png");
        if (bgFile.exists()) {
            Image bgImage = new Image(bgFile.toURI().toString());

            BackgroundSize bgSize = new BackgroundSize(
                    100, 100, true, true, true, false
            );

            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );

            card.setBackground(new Background(backgroundImage));
        } else {
            card.setStyle(
                    "-fx-background-color: #f8e7b5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #d6b36a;" +
                    "-fx-border-radius: 20;"
            );
        }

        // Rounded clip for the card
        Rectangle clip = new Rectangle(250, 430);
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        card.setClip(clip);

        Region topSpacer = new Region();
        topSpacer.setPrefHeight(30);

        ImageView image = new ImageView();

        // Load ingredient image if available
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            File file = new File(item.getImagePath());
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                image.setImage(img);
            }
        }

        image.setFitWidth(95);
        image.setFitHeight(95);
        image.setPreserveRatio(true);

        Region imageTextSpacer = new Region();
        imageTextSpacer.setPrefHeight(10);

        Label name = new Label(item.getItemName());
        name.setWrapText(true);
        name.setMaxWidth(200);
        name.setPrefHeight(45);
        name.setMinHeight(45);
        name.setMaxHeight(45);
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label stock = new Label("Stock: " + item.getStocks());
        if (item.isLowStock()) {
            stock.setStyle("-fx-font-size: 12px; -fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            stock.setStyle("-fx-font-size: 12px; -fx-text-fill: green; -fx-font-weight: bold;");
        }

        Label used = new Label("Used: " + item.getUsedCount() + " unit/s");
        used.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");

        Label price = new Label("Cost: ₱" + String.format("%.2f", item.getPrice()));
        price.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");

        Region spacer = new Region();
        spacer.setPrefHeight(55);

        // What happens when a card is clicked
        card.setOnMouseClicked(e -> {
            selectedInventoryItem = item;

            if (selectedInventoryCard != null) {
                setDefaultCardStyle(selectedInventoryCard);
            }

            setSelectedCardStyle(card);
            selectedInventoryCard = card;

            inventoryInfoArea.setText(
                    item.getItemName()
                    + "\n\nDescription: " + item.getDescription()
                    + "\n\nCurrent Stock: " + item.getStocks()
                    + "\nUsed Count: " + item.getUsedCount()
                    + "\nCost Per Unit: ₱" + String.format("%.2f", item.getPrice())
                    + "\nLow Stock Limit: " + item.getLowStockThreshold()
            );
        });

        card.getChildren().addAll(
                topSpacer,
                image,
                imageTextSpacer,
                name,
                stock,
                used,
                price,
                spacer
        );

        return card;
    }

    // Applies the normal style of a card
    private void setDefaultCardStyle(VBox card) {
        File bgFile = new File("images/card_bg.png");

        if (bgFile.exists()) {
            Image bgImage = new Image(bgFile.toURI().toString());

            BackgroundSize bgSize = new BackgroundSize(
                    100, 100, true, true, true, false
            );

            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );

            card.setBackground(new Background(backgroundImage));
            card.setBorder(new Border(new BorderStroke(
                    javafx.scene.paint.Paint.valueOf("#d6b36a"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(20),
                    new BorderWidths(1)
            )));
        } else {
            card.setStyle(
                    "-fx-background-color: #f8e7b5;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #d6b36a;" +
                    "-fx-border-radius: 20;"
            );
        }
    }

    // Applies the highlighted style of a selected card
    private void setSelectedCardStyle(VBox card) {
        File bgFile = new File("images/card_bg.png");

        if (bgFile.exists()) {
            Image bgImage = new Image(bgFile.toURI().toString());

            BackgroundSize bgSize = new BackgroundSize(
                    100, 100, true, true, true, false
            );

            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );

            card.setBackground(new Background(backgroundImage));
            card.setBorder(new Border(new BorderStroke(
                    javafx.scene.paint.Paint.valueOf("#ffb703"),
                    BorderStrokeStyle.SOLID,
                    new CornerRadii(20),
                    new BorderWidths(3)
            )));
        } else {
            card.setStyle(
                    "-fx-background-color: #fff3cd;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #ffb703;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 20;"
            );
        }
    }

    // Returns the root layout so AppController can display this screen
    public BorderPane getRoot() {
        return root;
    }
}