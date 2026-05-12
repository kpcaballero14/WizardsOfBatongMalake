package application;

import javafx.application.Application;
import javafx.stage.Stage;
import system.RetailSystem;

// It is responsible for starting the program.
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Create the main RetailSystem object.
        // This holds the data and business logic of the project.
        RetailSystem retailSystem = new RetailSystem();

        // Load the default data of the system.
        // This includes the starting users, inventory, menu items, and other initial values.
        retailSystem.initializeDefaultData();

        // Create the AppController.
        // AppController handles screen switching, alerts, music, and other app-wide controls.
        AppController app = new AppController(primaryStage, retailSystem);

        // Set the title shown at the top of the application window.
        primaryStage.setTitle("Wizards of Batong Malake");

        // Start the app in maximized mode.
        primaryStage.setMaximized(true);

        // Show the first screen of the app, which is the login screen.
        app.showLoginScreen();

        // Finally display the window.
        primaryStage.show();
    }

    // The main method launches the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}