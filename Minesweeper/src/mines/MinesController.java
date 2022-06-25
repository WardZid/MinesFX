package mines;

import java.io.File;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MinesController {
	/*
	 * The mines controller manually builds a grid that holds the game GUI. And is
	 * then added to the hbox of the game alongside the input fields and reset
	 * button that are prepared in the fxml file using scenebuilder
	 * 
	 * the grid contains buttons that correspond to each square in the game. the
	 * game logic is pulled from the Mines class
	 * 
	 */

	// hold the stage to be able to manipulate it from the controller
	private Stage stage;

	// Mines object to use as the game algorithm
	private Mines mineGame;

	// 2d array of stack panes to serve as the cells of the GridPane
	private StackPane[][] squares;

	// GridPane that holds the squares of the game including bombs, buttons and all
	private GridPane grid;

	// user inputs
	private int height, width, mines;
	
	//pre defined sizes
	private final int easyX=10;
	private final int easyY=10;
	private final int easyMines=10;
	
	private final int mediumX=15;
	private final int mediumY=15;
	private final int mediumMines=30;

	private final int hardX=20;
	private final int hardY=20;
	private final int hardMines=40;

	// hbox that contains the reset/entry fields and gridpane
	@FXML
	private HBox hbox;

	// user input text fields
	@FXML
	private TextField widthTF;
	@FXML
	private TextField heightTF;
	@FXML
	private TextField minesTF;

	// label that tells you if you lost or won
	@FXML
	private Label winLose;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void initialize() {
		newGame();
	}

	@FXML
	public void onEasy() {
		widthTF.setText(easyX+"");
		heightTF.setText(easyY+"");
		minesTF.setText(easyMines+"");
		newGame();
	}
	
	@FXML
	public void onMedium() {
		widthTF.setText(mediumX+"");
		heightTF.setText(mediumY+"");
		minesTF.setText(mediumMines+"");
		newGame();
	}
	
	@FXML
	public void onHard() {
		widthTF.setText(hardX+"");
		heightTF.setText(hardY+"");
		minesTF.setText(hardMines+"");
		newGame();
	}
	
	@FXML
	public void buildClicked() {
		// start fresh game
		newGame();
	}

	/**
	 *  newGame starts a new instance of Mines to prepare for a new game
	 */
	private void newGame() {
		if(hbox.getChildren().size()>1)
			// remove old gridpane to prepare for entry of another one
			hbox.getChildren().remove(1);
		
		// record entries (assuming they are legal)
		height = Integer.parseInt(heightTF.getText());
		width = Integer.parseInt(widthTF.getText());
		mines = Integer.parseInt(minesTF.getText());

		// new instance of Mines (new game with its own size and mine locations)
		mineGame = new Mines(height, width, mines);

		buildGrid();
	}

	// remove existing grid from the the hbox and rebuild the updated version
	private void updateGrid() {
		hbox.getChildren().remove(1);
		buildGrid();
	}

	// buildGrid() creates a new gridpane with each cell containing a stackpane
	// each stackpane can contain a button or a label
	// there are two types of buttons and two types of labels
	// a button can be blank, meaning it can be pressed to reveal whats behind or
	// flagged
	// or a button is flagged and can only be unflagged and then will turn into a
	// normal button
	// a label can contain two things, a png of a bomb (red or green, depending on
	// win/lose)
	// or the label can contain a number count of neighboring mines or just blank

	// *what the cells contain is determined by the mineGame object, which is an
	// instance of Mines
	private void buildGrid() {

		// check if on this build grid call, the game is over, and setshowall to true
		if (mineGame.isDone())
			mineGame.setShowAll(true);

		if (mineGame.gameOver()) {

			if (mineGame.gameWon()) {

				// play winning sound
				Media audio = new Media(new File("src/mines/sounds/Victory.mp3").toURI().toString());
				MediaPlayer mediaPlayer = new MediaPlayer(audio);
				mediaPlayer.play();

				// shows green "you won" label
				winLose.setText("You Won!");
				winLose.setStyle("-fx-background-color:green");
				winLose.setVisible(true);

			} else {
				// shows red "you lost" label
				winLose.setText("You Lost!");
				winLose.setStyle("-fx-background-color:red");
				winLose.setVisible(true);
			}

		} else
			// if game not over, keep the label hidden
			winLose.setVisible(false);

		// start new gridPane
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		
		grid.setPadding(new Insets(5));

		// init cell matrix
		squares = new StackPane[height][width];

		// for loop that inits each stackpane and enters correct contents as described
		// above
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {

				// init stackpane
				squares[i][j] = new StackPane();
				squares[i][j].setPrefSize(40, 40);
				squares[i][j].setMaxSize(40, 40);
				squares[i][j].setMinSize(10, 10);

				// check game state of each cell and init accordingly

				if (mineGame.get(i, j).equals("F")) { // if flagged button
					Button button = new Button();
					button.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

					// fetch flag png
					ImageView view = new ImageView(new Image("mines/icons/flag.png"));

					// make sure the size of the png is according to the button size
					view.fitWidthProperty().bind(squares[i][j].widthProperty().divide(2));
					view.setPreserveRatio(true); // keep the pic square
					button.setGraphic(view); // add png to button

					// flagged button can have only one action
					// which is to be unflagged
					// a flagged button cannot be clicked to reveal whats behind
					button.setOnMouseClicked(event -> {

						// fetch row and column of each from gridpane since we cant use non final vars
						int row = GridPane.getRowIndex(button.getParent()); // basically i
						int column = GridPane.getColumnIndex(button.getParent());// basically j

						// if right click
						if (event.getButton() == MouseButton.SECONDARY) {
							mineGame.toggleFlag(row, column); // simply toggle flag

							updateGrid();
						}
					});

					// add button to stackpane with matching coordinates
					squares[i][j].getChildren().add(button);

				} else if (mineGame.get(i, j).equals(".")) {

					// init normal button that can be flagged or opened
					Button button = new Button();
					button.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
//					button.setMinSize(50, 50);

					// click listener
					button.setOnMouseClicked(event -> {

						// fetch row and column of each from gridpane since we cant use non final vars
						int row = GridPane.getRowIndex(button.getParent()); // basically i
						int column = GridPane.getColumnIndex(button.getParent()); // basically j

						// if left click
						if (event.getButton() == MouseButton.PRIMARY) {
							// open the clicked mine
							if (!mineGame.open(row, column)) {
								// if false returned, set showall
								// game loss/win is handled in the Mines class
								mineGame.setShowAll(true);

								// fetch explosion mp3 and play the sound
								Media audio = new Media(
										new File("src/mines/sounds/ExplosionSound.mp3").toURI().toString());
								MediaPlayer mediaPlayer = new MediaPlayer(audio);
								mediaPlayer.play();
							}

							// if right click
						} else if (event.getButton() == MouseButton.SECONDARY)
							mineGame.toggleFlag(row, column); // simply toggle the flag

						// update the grid at the end of the event
						updateGrid();
					});

					// add button to stackpane with matching coordinates
					squares[i][j].getChildren().add(button);

				} else { // else if the square is open

					// init label with styling
					Label label = new Label();
					label.setAlignment(Pos.CENTER);
					label.setStyle("-fx-border-color:grey");
					label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
					label.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

					// if square contains mine (in which case the game is definitely over)
					if (mineGame.get(i, j).equals("X")) {
						// init the image view for the bomb png
						ImageView view = new ImageView(new Image("mines/icons/bomb.png"));

						// keep size proportionate to to the parent stackpane
						view.fitWidthProperty().bind(squares[i][j].widthProperty().divide(2));
						view.setPreserveRatio(true); // keep it square
						label.setGraphic(view); // add to label

						// if game is won, the background of the bomb will be green
						if (mineGame.gameWon())
							label.setStyle("-fx-background-color:green");
						else
							// if not, it will be red
							label.setStyle("-fx-background-color:red");

						// add label to stackpane with matching coordinates
						squares[i][j].getChildren().add(label);

					} else { // else, a blank or number(neighbor count) is returned
						// the styling for the label has already been done above
						label.setText(mineGame.get(i, j));
						squares[i][j].getChildren().add(label);
					}
				}
				// finally, add the stackpane to the grid in the appropriate cell
				grid.add(squares[i][j], j, i);
			}

		// add grid to the hbox
		hbox.getChildren().add(grid);

		// resize stage to fit its contents
		// the stage is null in the first run only, where resizing isn't needed anyway
		if (stage != null)
			stage.sizeToScene();
	}

}
