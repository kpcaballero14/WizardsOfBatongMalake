package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		String musicFile = "shop_music.mp3";     // For example
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.play();
		StackPane root = new StackPane();
		primaryStage.setScene(new Scene(root, 800, 450));
		primaryStage.show();

		Pane root = new Pane();

		try {
			Image backgroundImage = new Image("file:background.jpg"); // placeholder
			BackgroundImage bgImage = new BackgroundImage(
				backgroundImage,
                    BackgroundRepeat.NO_REPEAT, // X repeat
                    BackgroundRepeat.NO_REPEAT, // Y repeat
                    BackgroundPosition.CENTER, // Position
					new BackgroundSize(
						BackgroundSize.AUTO, BackgroundSize.AUTO,
						false, false, true, false
					)
			);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
