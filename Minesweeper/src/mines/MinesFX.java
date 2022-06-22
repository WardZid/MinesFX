package mines;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MinesFX extends Application {

	// static intance of the controller to pass it any info it might need
	private static MinesController minesController;

	public static void main(String[] args) {
		launch(args);

	}
	
	
	public void start(Stage primaryStage) throws Exception {
		try {
			// fetch pre-made fxml file that details the gui's appearance
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MinesFXML.fxml"));
			Parent root = loader.load();

			//fetch the static controller intance from the fxml loader
			minesController = loader.getController();
			minesController.setStage(primaryStage);	//pass the stage to the controller

			primaryStage.setTitle("Minesweeper"); // window title
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
