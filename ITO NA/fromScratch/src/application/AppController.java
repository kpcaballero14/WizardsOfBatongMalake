package application;

import java.io.File;

import application.screens.CashTransactionScreen;
import application.screens.DashboardScreen;
import application.screens.InventoryScreen;
import application.screens.LoginScreen;
import application.screens.MenuManagementScreen;
import application.screens.SalesScreen;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.User;
import system.RetailSystem;

// AppController is the main controller of the whole application.
// It manages screen switching, current user tracking,
// background music, window icon, and alert popups.
public class AppController {

    // The main JavaFX window.
    private Stage stage;

    // The main system object containing the business logic and data.
    private RetailSystem retailSystem;

    // The currently logged-in user.
    private User currentUser;

    // The media player used for background music.
    private MediaPlayer backgroundMusicPlayer;

    // Constructor of AppController.
    public AppController(Stage stage, RetailSystem retailSystem) {
        this.stage = stage;
        this.retailSystem = retailSystem;

        // Load the logo and use it as the window icon if it exists.
        File logoFile = new File("images/Logo.png");
        if (logoFile.exists()) {
            this.stage.getIcons().add(new Image(logoFile.toURI().toString()));
        }

        // Start background music.
        initializeBackgroundMusic();

        // Stop the music when the app window is closed.
        this.stage.setOnCloseRequest(e -> stopBackgroundMusic());
    }

    // Returns the RetailSystem object.
    // Screens use this to access inventory, menu,
    // sales, and cash transaction data.
    public RetailSystem getRetailSystem() {
        return retailSystem;
    }

    // Returns the currently logged-in user.
    public User getCurrentUser() {
        return currentUser;
    }

    // Updates the currently logged-in user.
    // Usually called right after a successful login.
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // This method changes the visible screen of the application.
    // It creates a new Scene using the given root pane,
    // applies the CSS file,
    // adjusts the size to the screen bounds,
    // and maximizes the stage.
    public void switchScene(Pane root) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(
                root,
                screenBounds.getWidth(),
                screenBounds.getHeight()
        );

        // Apply the global CSS styling file.
        scene.getStylesheets().add(
                getClass().getResource("/application/application.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setMaximized(true);
    }

    // Shows the login screen.
    public void showLoginScreen() {
        switchScene(new LoginScreen(this).getRoot());
    }

    // Shows the manager dashboard.
    public void showManagerDashboard() {
        switchScene(new DashboardScreen(this, true).getRoot());
    }

    // Shows the cashier dashboard.
    public void showCashierDashboard() {
        switchScene(new DashboardScreen(this, false).getRoot());
    }

    // Shows the sales management screen.
    public void showSalesScreen() {
        switchScene(new SalesScreen(this).getRoot());
    }

    // Shows the inventory management screen.
    public void showInventoryScreen() {
        switchScene(new InventoryScreen(this).getRoot());
    }

    // Shows the menu management screen.
    public void showMenuScreen() {
        switchScene(new MenuManagementScreen(this).getRoot());
    }

    // Shows the cash transaction management screen.
    public void showCashTransactionScreen() {
        switchScene(new CashTransactionScreen(this).getRoot());
    }

    // Sends the user back to the correct dashboard depending on role.
    // If the current user is a Manager, open the manager dashboard.
    // Otherwise, open the cashier dashboard.
    public void goBackToDashboard() {
        if (currentUser != null && currentUser.getRole().equals("Manager")) {
            showManagerDashboard();
        } else {
            showCashierDashboard();
        }
    }

    // Shows an alert popup with a title and message.
    // Used for errors, warnings, and simple notifications.
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Loads and starts the background music.
    // The file should be found at sounds/shop_music.mp3.
    // If the file is missing, the program does not crash.
    private void initializeBackgroundMusic() {
        try {
            File musicFile = new File("sounds/shop_music.mp3");

            if (!musicFile.exists()) {
                System.out.println("Background music file not found: " + musicFile.getAbsolutePath());
                return;
            }

            Media media = new Media(musicFile.toURI().toString());
            backgroundMusicPlayer = new MediaPlayer(media);

            // Make the music loop continuously.
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // Lower the volume so it is not too loud.
            backgroundMusicPlayer.setVolume(0.3);

            // Start playing the music.
            backgroundMusicPlayer.play();

        } catch (Exception e) {
            System.out.println("Failed to load background music.");
            e.printStackTrace();
        }
    }

    // Pauses the background music.
    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.pause();
        }
    }

    // Resumes the background music after pausing.
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }
    }

    // Stops the background music completely.
    // Usually used when the app closes.
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }
}