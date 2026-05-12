package application.ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScreenHeader {

    // Creates a reusable header for screens
    // This includes the logo, title, and subtitle
    public static VBox createHeader(String titleText) {
        // Main horizontal container for the header
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 10, 20, 10));

        // Logo image
        ImageView logoView = new ImageView();

        try {
            // Load the logo file from the images folder
            File logoFile = new File("images/Logo.png");
            if (logoFile.exists()) {
                Image logo = new Image(logoFile.toURI().toString());
                logoView.setImage(logo);
            } else {
                System.out.println("Logo not found: " + logoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Failed to load logo.");
            e.printStackTrace();
        }

        // Set logo size
        logoView.setFitWidth(60);
        logoView.setFitHeight(60);
        logoView.setPreserveRatio(true);

        // Main title of the current screen
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Small subtitle shown below the title
        Label subtitle = new Label("Wizards of Batong Malake");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #8b5a00;");

        // Holds the title and subtitle vertically
        VBox textBox = new VBox(4, title, subtitle);
        textBox.setAlignment(Pos.CENTER_LEFT);

        // Spacer pushes the content to the left
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add logo, text, and spacer to the top bar
        topBar.getChildren().addAll(logoView, textBox, spacer);

        // Return the whole header as a VBox
        return new VBox(topBar);
    }
}