import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class WordGuessClient extends Application {

	TextField portBox;
	Text portText;
	TextField ipBox;
	Text ipText;
	TextField letterGuessBox;
	Text letterText;
	Text wordText;
	Pane mainScenePane;
	Button openingScreenButton;
	Button gameButton;
	Button sportsButton;
	Button foodButton;
	Button guessButton;
	GuessClient clientConnection;
	HashMap<String, Scene> sceneMap = new HashMap<>();
	ListView<String> listItems2;
	int currentWins = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("(Client) Word Guess!!!");

		// set up start screen:
		portBox = new TextField("5555");

		// prevent user from being able to enter more than 4 characters for port
		portBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 4 ? change : null));


		// textbox for entering port
		portText = new Text("Enter a port:");
		portText.setFont(Font.font ("Verdana", 20));
		portText.setStyle("-fx-font-weight: bold");
		portText.setFill(Color.WHITE);

		// textbox for entering ip address
		ipBox = new TextField("127.0.0.1");
		ipText = new Text("Enter an IP:");
		ipText.setFont(Font.font ("Verdana", 20));
		ipText.setStyle("-fx-font-weight: bold");
		ipText.setFill(Color.WHITE);

		// buttons for the first two scenes
		openingScreenButton = new Button("Start Guess the Word!");
		gameButton = new Button("Video Games");
		sportsButton = new Button("Sports");
		foodButton = new Button("Food");

		// all the necessary Texts, TextFields, and buttons needed for the game scene
		letterGuessBox = new TextField();
		letterText = new Text();
		guessButton = new Button("Take your guess!");
		listItems2 = new ListView<String>();
		listItems2.setPrefSize(800,100);
		letterGuessBox.setPrefSize(25,10);

		// prevent user from being able to enter more than 1 character for a guess
		letterGuessBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 1 ? change : null));

		//sets up the start screen
		Pane startPane = new Pane();
		startPane.setBackground(new Background(new BackgroundImage(new Image("randomLetters.png", 532, 720, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
		startPane.getChildren().addAll(ipText, ipBox, portText, portBox, openingScreenButton);
		portText.relocate(185, 200);
		portBox.relocate(165,230);
		ipText.relocate(190,300);
		ipBox.relocate(165, 330);
		openingScreenButton.relocate(170, 400);
		sceneMap.put("start screen", new Scene(startPane, 532, 720));

		//Choosing category scene set up
		mainScenePane = new Pane();
		mainScenePane.setBackground(new Background(new BackgroundImage(new Image("chooseCategory.jpg", 400, 470, true,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
		gameButton.setPrefSize(370,  120);
		gameButton.setStyle("-fx-font-size:40");
		sportsButton.setPrefSize(370,  120);
		sportsButton.setStyle("-fx-font-size:40");
		foodButton.setPrefSize(370,  120);
		foodButton.setStyle("-fx-font-size:40");
		mainScenePane.getChildren().addAll(gameButton, sportsButton, foodButton);
		gameButton.relocate(10, 50);
		sportsButton.relocate(10, 180);
		foodButton.relocate(10,310);
		sceneMap.put("main screen", new Scene(mainScenePane,400,470));

		//The guessing portion of the screen set up
		Pane gameScreenPane = new Pane();
		gameScreenPane.getChildren().addAll(guessButton, letterGuessBox, letterText, listItems2);
		guessButton.relocate(400, 475);
		letterGuessBox.relocate(445, 450);
		listItems2.relocate(100,725);

		clientConnection = new GuessClient(data->{}, 0,"00");

		// "Start Guess the Word!" button
		openingScreenButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				clientConnection = new GuessClient(data->{
					Platform.runLater(()->{listItems2.getItems().add(data.toString());
						int lastMessage = listItems2.getItems().size();
						listItems2.scrollTo(lastMessage);
						});
				}, Integer.parseInt(portBox.getText()), ipBox.getText());
				clientConnection.start();

				primaryStage.setScene(sceneMap.get("main screen"));
			}
		});

		gameButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Video Games");

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				wordText = new Text(clientConnection.clientInfo.getWord());
				wordText.setFont(Font.font ("Verdana", 20));
				wordText.setStyle("-fx-font-weight: bold");
				wordText.setFill(Color.RED);
				gameScreenPane.setBackground(new Background(new BackgroundImage(new Image("videoGames.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				gameScreenPane.getChildren().add(wordText);
				wordText.relocate(500,500);
				sceneMap.put("Game Screen", new Scene(gameScreenPane, 1000, 900));
				primaryStage.setScene(sceneMap.get("Game Screen"));
			}
		});

		sportsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Sports");
				gameScreenPane.setBackground(new Background(new BackgroundImage(new Image("sports.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				sceneMap.put("Game Screen", new Scene(gameScreenPane, 1000, 900));
				primaryStage.setScene(sceneMap.get("Game Screen"));
			}
		});

		foodButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Foods");
				gameScreenPane.setBackground(new Background(new BackgroundImage(new Image("food.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				sceneMap.put("Game Screen", new Scene(gameScreenPane, 1000, 900));
				primaryStage.setScene(sceneMap.get("Game Screen"));
			}
		});

		guessButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(Character.isLetter(letterGuessBox.getText().charAt(0)) && letterGuessBox.getText().length() == 1){
					clientConnection.send(letterGuessBox.getText());
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wordText.setText(clientConnection.clientInfo.getWord());

				if(clientConnection.clientInfo.getNumWordsGuessed() != currentWins){
					currentWins++;
					primaryStage.setScene(sceneMap.get("main screen"));
				}
			}
		});


		//if window is closed the program exits out
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});

		// show the start screen
		primaryStage.setScene(sceneMap.get("start screen"));
		primaryStage.show();
	}


}
