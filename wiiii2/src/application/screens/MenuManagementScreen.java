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
import model.MenuItem;
import javafx.scene.shape.Rectangle;

public class MenuManagementScreen {
    // Reference to the main app controller
    private AppController app;

    // Root layout of this screen
    private BorderPane root;

    // The currently selected menu item
    private MenuItem selectedMenuItem;

    // The currently selected card in the menu grid
    private VBox selectedMenuCard;

    // Text area that shows the details of the selected menu item
    private TextArea menuInfoArea;

    // Constructor
    public MenuManagementScreen(AppController app) {
        this.app = app;
        build();
    }

    // Builds the whole menu management screen
    private void build() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-menu");

        // Header with logo and title
        root.setTop(ScreenHeader.createHeader("Menu Management"));

        // Grid for showing all menu item cards
        TilePane menuGrid = new TilePane();
        menuGrid.setPadding(new Insets(140, 150, 150, 160));
        menuGrid.setHgap(10);
        menuGrid.setVgap(10);
        menuGrid.setPrefColumns(3);
        menuGrid.setStyle("-fx-background-color: transparent;");

        // Add one card for each menu item
        for (MenuItem item : app.getRetailSystem().getMenu().getItems()) {
            menuGrid.getChildren().add(createMenuCard(item));
        }

        // Scroll pane for the card grid
        ScrollPane scrollPane = new ScrollPane(menuGrid);
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

        // Area for showing menu item details
        menuInfoArea = new TextArea();
        menuInfoArea.setEditable(false);
        menuInfoArea.setWrapText(true);
        menuInfoArea.setPrefHeight(260);
        menuInfoArea.setPromptText("Click a menu item to view details.");
        menuInfoArea.setStyle("-fx-text-fill: black;");

        // Field for entering the new profit percentage
        TextField profitField = new TextField();
        profitField.setPromptText("New profit percentage");

        // Button to update profit percentage
        Button updateProfitBtn = new Button("Update Profit % and Recompute Price");
        updateProfitBtn.setMaxWidth(Double.MAX_VALUE);

        updateProfitBtn.setOnAction(e -> {
            // Make sure a menu item is selected first
            if (selectedMenuItem == null) {
                app.showAlert("No Menu Item Selected", "Please select a menu item first.");
                return;
            }

            try {
                double newProfit = Double.parseDouble(profitField.getText());

                // Update profit percentage
                selectedMenuItem.setProfitPercentage(newProfit);
                app.getRetailSystem().saveToTextFile();
                profitField.clear();

                app.showAlert(
                        "Price Updated",
                        selectedMenuItem.getItemName() + " now has a selling price of ₱"
                                + String.format("%.2f", selectedMenuItem.getPrice())
                );

                // Refresh screen after update
                app.showMenuScreen();

            } catch (Exception ex) {
                app.showAlert("Invalid Input", "Please enter a valid profit percentage.");
            }
        });

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> app.goBackToDashboard());

        // Labels on the right panel
        Label detailsLabel = new Label("Menu Item Details");
        detailsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label controlsLabel = new Label("Price and Profit Controls");
        controlsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Right side panel for details and controls
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(340);
        rightPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 15;"
        );

        rightPanel.getChildren().addAll(
                detailsLabel,
                menuInfoArea,
                controlsLabel,
                profitField,
                updateProfitBtn,
                backBtn
        );

        // Put the menu card grid in the center and controls on the right
        root.setCenter(scrollPane);
        root.setRight(rightPanel);
    }

    // Creates one menu item card
    private VBox createMenuCard(MenuItem item) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(16, 16, 16, 16));
        card.setPrefWidth(175);
        card.setMinWidth(175);
        card.setMaxWidth(175);
        card.setPrefHeight(175);
        card.setMinHeight(175);
        card.setMaxHeight(175);
        
        // Rounded clip for the card
        Rectangle clip = new Rectangle(250, 430);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        card.setClip(clip);

        Region topSpacer = new Region();
        topSpacer.setPrefHeight(30);

        ImageView image = new ImageView();

        // Load menu item image if available
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            File file = new File(item.getImagePath());
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                image.setImage(img);
            }
        }

        image.setFitWidth(110);
        image.setFitHeight(110);
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

        Label description = new Label(item.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(200);
        description.setPrefHeight(70);
        description.setMinHeight(70);
        description.setMaxHeight(70);
        description.setAlignment(Pos.TOP_CENTER);
        description.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");

        Label price = new Label("₱" + String.format("%.2f", item.getPrice()));
        price.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #8b5a00;");

        Label ingredientCost = new Label("Ingredient Cost: ₱" + String.format("%.2f", item.getIngredientCost()));
        ingredientCost.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");

        Label profit = new Label("Profit: " + item.getProfitPercentage() + "%");
        profit.setStyle("-fx-font-size: 11px; -fx-text-fill: black;");

        Region spacer = new Region();
        spacer.setPrefHeight(20);
        
        card.setOnMouseEntered(e -> {
            selectedMenuItem = item;
            image.setFitWidth(120);
            image.setFitHeight(120);
            

            if (selectedMenuCard != null) {
                setDefaultCardStyle(selectedMenuCard);
            }

            setSelectedCardStyle(card);
            selectedMenuCard = card;
        
        });
        
        card.setOnMouseExited(e -> {
        	selectedMenuItem = item;
            image.setFitWidth(110);
            image.setFitHeight(110);
        });

        // What happens when a card is clicked
        card.setOnMouseClicked(e -> {
            selectedMenuItem = item;

            if (selectedMenuCard != null) {
                setDefaultCardStyle(selectedMenuCard);
            }

            setSelectedCardStyle(card);
            selectedMenuCard = card;

            menuInfoArea.setText(
                    item.getItemName().toUpperCase()
                    + "\n\nDescription: " + item.getDescription()
                    + "\n\nIngredient Cost: ₱" + String.format("%.2f", item.getIngredientCost())
                    + "\nProfit Percentage: " + item.getProfitPercentage() + "%"
                    + "\nSelling Price: ₱" + String.format("%.2f", item.getPrice())
                    + "\n\nIngredients:\n" + item.getIngredientsSummary()
            );
        });

        card.getChildren().addAll(
                topSpacer,
                image,
                spacer
        );

        return card;
    }

    // Applies the normal style of a card
    private void setDefaultCardStyle(VBox card) {
//            card.setBorder(new Border(new BorderStroke(
//                    javafx.scene.paint.Paint.valueOf("#d6b36a"),
//                    BorderStrokeStyle.SOLID,
//                    new CornerRadii(20),
//                    new BorderWidths(1)
//            )));
            card.setStyle(
                    "-fx-background-radius: 20;"
            );
        }

    // Applies the highlighted style of a selected card
    private void setSelectedCardStyle(VBox card) {
//            card.setBorder(new Border(new BorderStroke(
//                    javafx.scene.paint.Paint.valueOf("#ffb703"),
//                    BorderStrokeStyle.SOLID,
//                    new CornerRadii(15),
//                    new BorderWidths(3)
//            )));
            card.setStyle(
                    "-fx-background-radius: 20;" +
                    "-fx-border-color: #ffb703;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 20;"
            );
        }
    

    // Returns the root layout so AppController can display this screen
    public BorderPane getRoot() {
        return root;
    }
}