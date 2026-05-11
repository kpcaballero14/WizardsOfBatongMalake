package application.screens;

import java.io.File;

import application.AppController;
import application.ui.ScreenHeader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import model.MenuItem;
import model.SaleTransaction;

public class SalesScreen {
    // Reference to the main app controller
    private AppController app;

    // Root layout of this screen
    private BorderPane root;

    // Constructor
    public SalesScreen(AppController app) {
        this.app = app;
        build();
    }

    // Builds the whole sales screen
    private void build() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-screen");

        // Header with logo and title
        root.setTop(ScreenHeader.createHeader("Sales Management"));

        // Grid for showing all menu item cards
        TilePane menuGrid = new TilePane();
        menuGrid.setPadding(new Insets(20));
        menuGrid.setHgap(20);
        menuGrid.setVgap(20);
        menuGrid.setPrefColumns(4);
        menuGrid.setStyle("-fx-background-color: transparent;");

        // Temporary list that acts as the current cart/order
        ObservableList<SaleTransaction> cart = FXCollections.observableArrayList();

        // Label showing total price of the cart
        Label totalLabel = new Label("Total: ₱0.00");
        totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Table showing the items currently in the cart
        TableView<SaleTransaction> cartTable = new TableView<>();

        TableColumn<SaleTransaction, String> cartItemCol = new TableColumn<>("Item");
        cartItemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<SaleTransaction, Integer> cartQtyCol = new TableColumn<>("Qty");
        cartQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<SaleTransaction, Double> cartTotalCol = new TableColumn<>("Total");
        cartTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        cartTable.getColumns().addAll(cartItemCol, cartQtyCol, cartTotalCol);
        cartTable.setItems(cart);
        cartTable.setPrefHeight(220);

        // Create one card for each menu item
        for (MenuItem item : app.getRetailSystem().getMenu().getItems()) {
            menuGrid.getChildren().add(createSalesMenuCard(item, cart, totalLabel));
        }

        // Scroll pane for the menu cards
        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                "-fx-background-color: transparent;"
        );

        // Makes the scroll viewport transparent too
        scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    if (scrollPane.lookup(".viewport") != null) {
                        scrollPane.lookup(".viewport").setStyle("-fx-background-color: transparent;");
                    }
                });
            }
        });

        // Field for payment input
        TextField amountPaidField = new TextField();
        amountPaidField.setPromptText("Amount Paid");

        // Area for receipt output
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setWrapText(true);
        receiptArea.setPrefHeight(180);
        receiptArea.setPromptText("Receipt will appear here.");

        // Removes the selected item from the cart table
        Button removeBtn = new Button("Remove Selected Item");
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setOnAction(e -> {
            SaleTransaction selected = cartTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                app.showAlert("No Item Selected", "Please select an item from the cart.");
                return;
            }

            cart.remove(selected);
            updateCartTotal(cart, totalLabel);
        });

        // Clears the entire cart
        Button clearCartBtn = new Button("Clear Order");
        clearCartBtn.setMaxWidth(Double.MAX_VALUE);
        clearCartBtn.setOnAction(e -> {
            cart.clear();
            updateCartTotal(cart, totalLabel);
            receiptArea.clear();
        });

        // Finalizes the order
        Button checkoutBtn = new Button("Checkout Order");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setStyle(
                "-fx-background-color: #d62828;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        checkoutBtn.setOnAction(e -> {
            // Do not allow checkout if cart is empty
            if (cart.isEmpty()) {
                app.showAlert("Empty Order", "Please add at least one item to the order.");
                return;
            }

            try {
                double amountPaid = Double.parseDouble(amountPaidField.getText());
                double total = getCartTotal(cart);

                // Check if payment is enough
                if (amountPaid < total) {
                    app.showAlert("Insufficient Payment", "Amount paid is less than the total.");
                    return;
                }

                // Process each cart item as a sale
                for (SaleTransaction cartItem : cart) {
                    MenuItem matchingMenuItem = findMenuItemByName(cartItem.getItemName());

                    if (matchingMenuItem == null) {
                        app.showAlert("Error", "Menu item not found.");
                        return;
                    }

                    SaleTransaction sale = app.getRetailSystem().processSale(
                            matchingMenuItem,
                            cartItem.getQuantity(),
                            cartItem.getTotalAmount()
                    );

                    // Stop if stock is not enough
                    if (sale == null) {
                        app.showAlert(
                                "Sale Failed",
                                "Insufficient stock for " + matchingMenuItem.getItemName()
                        );
                        return;
                    }
                }

                double change = amountPaid - total;

                // Build the receipt text
                StringBuilder receipt = new StringBuilder();
                receipt.append("WIZARDS OF BATONG MALAKE\n");
                receipt.append("SALE RECEIPT\n\n");

                for (SaleTransaction item : cart) {
                    receipt.append(item.getItemName())
                           .append(" x")
                           .append(item.getQuantity())
                           .append(" = ₱")
                           .append(String.format("%.2f", item.getTotalAmount()))
                           .append("\n");
                }

                receipt.append("\nTotal: ₱").append(String.format("%.2f", total));
                receipt.append("\nAmount Paid: ₱").append(String.format("%.2f", amountPaid));
                receipt.append("\nChange: ₱").append(String.format("%.2f", change));

                receiptArea.setText(receipt.toString());

                // Save updated system data
                app.getRetailSystem().saveToTextFile();

                // Clear cart after successful checkout
                cart.clear();
                updateCartTotal(cart, totalLabel);
                amountPaidField.clear();

            } catch (Exception ex) {
                app.showAlert("Invalid Input", "Please enter a valid payment amount.");
            }
        });

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> app.goBackToDashboard());

        // Title of the order panel
        Label orderLabel = new Label("Your Order");
        orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Right panel containing cart and order controls
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(360);
        rightPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 15;"
        );

        rightPanel.getChildren().addAll(
                orderLabel,
                cartTable,
                totalLabel,
                amountPaidField,
                checkoutBtn,
                removeBtn,
                clearCartBtn,
                receiptArea,
                backBtn
        );

        // Put menu cards in center and order panel on the right
        root.setCenter(scrollPane);
        root.setRight(rightPanel);
    }

    // Creates one menu card for the sales screen
    private VBox createSalesMenuCard(MenuItem item, ObservableList<SaleTransaction> cart, Label totalLabel) {
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

        // Spacer at the top of the card
        Region topSpacer = new Region();
        topSpacer.setPrefHeight(150);

        ImageView image = new ImageView();

        // Load menu item image if available
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

        // Menu item name
        Label name = new Label(item.getItemName());
        name.setWrapText(true);
        name.setMaxWidth(200);
        name.setPrefHeight(45);
        name.setMinHeight(45);
        name.setMaxHeight(45);
        name.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Menu item description
        Label description = new Label(item.getDescription());
        description.setWrapText(true);
        description.setMaxWidth(200);
        description.setPrefHeight(70);
        description.setMinHeight(70);
        description.setMaxHeight(70);
        description.setAlignment(Pos.TOP_CENTER);
        description.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");

        // Price label
        Label price = new Label("₱" + String.format("%.2f", item.getPrice()));
        price.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #d62828;");

        // Spacer before controls
        Region spacer = new Region();
        spacer.setPrefHeight(70);

        // Quantity label
        Label quantityLabel = new Label("Quantity");
        quantityLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Spinner for choosing quantity
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setMaxWidth(110);

        // Button to add item to cart
        Button addBtn = new Button("Add to Order");
        addBtn.setPrefWidth(180);
        addBtn.setPrefHeight(34);
        addBtn.setStyle(
                "-fx-background-color: #ffb703;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        addBtn.setOnAction(e -> {
            int quantity = quantitySpinner.getValue();

            SaleTransaction existingItem = null;

            // Check if item is already in cart
            for (SaleTransaction cartItem : cart) {
                if (cartItem.getItemName().equals(item.getItemName())) {
                    existingItem = cartItem;
                    break;
                }
            }

            // If item already exists, increase quantity
            if (existingItem != null) {
                cart.remove(existingItem);

                SaleTransaction updatedItem = new SaleTransaction(
                        item.getItemName(),
                        existingItem.getQuantity() + quantity,
                        item.getPrice(),
                        item.getPrice() * (existingItem.getQuantity() + quantity)
                );

                cart.add(updatedItem);
            } else {
                // Otherwise add a new item to cart
                SaleTransaction newItem = new SaleTransaction(
                        item.getItemName(),
                        quantity,
                        item.getPrice(),
                        item.getPrice() * quantity
                );

                cart.add(newItem);
            }

            updateCartTotal(cart, totalLabel);
            quantitySpinner.getValueFactory().setValue(1);
        });

        card.getChildren().addAll(
                topSpacer,
                image,
                name,
                description,
                price,
                spacer,
                quantityLabel,
                quantitySpinner,
                addBtn
        );

        return card;
    }

    // Finds a menu item by its name
    private MenuItem findMenuItemByName(String itemName) {
        for (MenuItem menuItem : app.getRetailSystem().getMenu().getItems()) {
            if (menuItem.getItemName().equals(itemName)) {
                return menuItem;
            }
        }
        return null;
    }

    // Computes the total price of all cart items
    private double getCartTotal(ObservableList<SaleTransaction> cart) {
        double total = 0;

        for (SaleTransaction item : cart) {
            total += item.getTotalAmount();
        }

        return total;
    }

    // Updates the total label based on the cart
    private void updateCartTotal(ObservableList<SaleTransaction> cart, Label totalLabel) {
        totalLabel.setText("Total: ₱" + String.format("%.2f", getCartTotal(cart)));
    }

    // Returns the root layout so AppController can display this screen
    public BorderPane getRoot() {
        return root;
    }
}