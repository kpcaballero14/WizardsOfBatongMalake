package application.screens;

import application.AppController;
import application.ui.ScreenHeader;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.CashTransaction;

public class CashTransactionScreen {
    // Reference to the main app controller
    private AppController app;

    // Root layout of this screen
    private BorderPane root;

    // Constructor
    public CashTransactionScreen(AppController app) {
        this.app = app;
        build();
    }

    // Builds the whole cash transaction screen
    private void build() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-screen");

        // Header with logo and title
        root.setTop(ScreenHeader.createHeader("Cash Transaction Management"));

        // Shows the current cash balance
        Label balanceLabel = new Label(
                "Current Balance: ₱" + String.format("%.2f", app.getRetailSystem().getCashBalance())
        );
        balanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Table for all cash transactions
        TableView<CashTransaction> table = new TableView<>();

        TableColumn<CashTransaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<CashTransaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<CashTransaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<CashTransaction, Object> dateCol = new TableColumn<>("Date and Time");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));

        table.getColumns().addAll(typeCol, descCol, amountCol, dateCol);
        table.setItems(FXCollections.observableArrayList(app.getRetailSystem().getCashTransactions()));

        // Input for description
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        // Input for amount
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        // Button to add cash in
        Button cashInBtn = new Button("Add Cash In");
        cashInBtn.setMaxWidth(Double.MAX_VALUE);

        cashInBtn.setOnAction(e -> {
            try {
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());

                // Do not allow empty description
                if (description.isEmpty()) {
                    app.showAlert("Invalid Input", "Please enter a description.");
                    return;
                }

                // Add cash in to the system
                app.getRetailSystem().addCashIn(description, amount);

                // Refresh balance and table
                balanceLabel.setText(
                        "Current Balance: ₱" + String.format("%.2f", app.getRetailSystem().getCashBalance())
                );
                table.setItems(FXCollections.observableArrayList(app.getRetailSystem().getCashTransactions()));

                // Clear inputs
                descriptionField.clear();
                amountField.clear();

            } catch (Exception ex) {
                app.showAlert("Invalid Input", "Please enter a valid cash-in amount.");
            }
        });

        // Button to add cash out
        Button cashOutBtn = new Button("Add Cash Out");
        cashOutBtn.setMaxWidth(Double.MAX_VALUE);

        cashOutBtn.setOnAction(e -> {
            try {
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());

                // Do not allow empty description
                if (description.isEmpty()) {
                    app.showAlert("Invalid Input", "Please enter a description.");
                    return;
                }

                // Do not allow cash out bigger than current balance
                if (amount > app.getRetailSystem().getCashBalance()) {
                    app.showAlert("Insufficient Balance", "Cash out amount is greater than current balance.");
                    return;
                }

                // Add cash out to the system
                app.getRetailSystem().addCashOut(description, amount);

                // Refresh balance and table
                balanceLabel.setText(
                        "Current Balance: ₱" + String.format("%.2f", app.getRetailSystem().getCashBalance())
                );
                table.setItems(FXCollections.observableArrayList(app.getRetailSystem().getCashTransactions()));

                // Clear inputs
                descriptionField.clear();
                amountField.clear();

            } catch (Exception ex) {
                app.showAlert("Invalid Input", "Please enter a valid cash-out amount.");
            }
        });

        // Back button returns to dashboard
        Button backBtn = new Button("Back");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> app.goBackToDashboard());

        // Right panel for controls
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(320);
        rightPanel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.88);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #d6b36a;" +
                "-fx-border-radius: 15;"
        );

        rightPanel.getChildren().addAll(
                balanceLabel,
                descriptionField,
                amountField,
                cashInBtn,
                cashOutBtn,
                backBtn
        );

        // Table goes at the center, controls go at the right
        root.setCenter(table);
        root.setRight(rightPanel);
    }

    // Returns the root layout so AppController can show this screen
    public BorderPane getRoot() {
        return root;
    }
}