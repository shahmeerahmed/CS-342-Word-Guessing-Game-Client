import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class WordGuessClient extends Application {

	//all the TextFields and texts needed for the program
	TextField portBox = new TextField("5555");
	Text guessesLeft = new Text();
	Text portText = new Text("Enter a port:");
	TextField ipBox = new TextField("127.0.0.1");
	Text ipText = new Text("Enter an IP:");
	TextField letterGuessBox = new TextField();
	Text letterText = new Text();
	Text wordText = new Text();

	//all the buttons needed
	Button openingScreenButton = new Button("Start Guess the Word!");
	Button gameButton = new Button("Video Games");
	Button sportsButton = new Button("Sports");
	Button foodButton = new Button("Food");
	Button guessButton = new Button("Take your guess!");
	Button playAgain = new Button("Play Again");
	Button quitButton = new Button("Quit");

	//all the panes needed for the program
	Pane startPane = new Pane();
	Pane mainScenePane = new Pane();
	Pane gameScreenPane = new Pane();
	Pane sportsPane = new Pane();
	Pane foodPane = new Pane();
	Pane winPane = new Pane();
	Pane losePane = new Pane();

	//Extra variables needed
	String fontFamily = "Haettenschweiler";
	GuessClient clientConnection;
	HashMap<String, Scene> sceneMap = new HashMap<>();
	ListView<String> listItems = new ListView<String>();
	int currentWins = 0;
	int guessedWrong = 0;

	//All music files needed
	Media intro = new Media(getClass().getClassLoader().getResource("Title Screen.mp3").toString());
	MediaPlayer introSong = new MediaPlayer(intro);

	Media mainMenu = new Media(getClass().getClassLoader().getResource("Main Menu.mp3").toString());
	MediaPlayer mainMenuSong = new MediaPlayer(mainMenu);

	Media win = new Media(getClass().getClassLoader().getResource("Queen - We Are The Champions (Official Video).mp3").toString());
	MediaPlayer winSong = new MediaPlayer(win);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("(Client) Word Guess!!!");

		// prevent user from being able to enter more than 4 characters for port
		portBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 4 ? change : null));

		// styling the text for displaying enter port
		portText.setFont(Font.font (fontFamily, 20));
		portText.setStyle("-fx-font-weight: bold");
		portText.setFill(Color.WHITE);

		//styling the text for displaying Enter an ip address
		ipText.setFont(Font.font (fontFamily, 20));
		ipText.setStyle("-fx-font-weight: bold");
		ipText.setFill(Color.WHITE);

		// changing the size of the listView
		listItems.setPrefSize(800,100);

		// prevent user from being able to enter more than 1 character for a guess
		letterGuessBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 1 ? change : null));

		//sets up the start screen
		startPane.setBackground(new Background(new BackgroundImage(new Image("randomLetters.png", 532, 720, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
		startPane.getChildren().addAll(ipText, ipBox, portText, portBox, openingScreenButton);
		portText.relocate(185, 200);
		portBox.relocate(165,230);
		ipText.relocate(190,300);
		ipBox.relocate(165, 330);
		openingScreenButton.relocate(170, 400);
		sceneMap.put("start screen", new Scene(startPane, 532, 720));

		//Choosing category scene
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
		guessesLeft.setFont(Font.font (fontFamily, 40));
		guessesLeft.setStyle("-fx-font-weight: bold");
		guessesLeft.setFill(Color.WHITE);
		wordText.setFont(Font.font (fontFamily, 40));
		wordText.setStyle("-fx-font-weight: bold");
		wordText.setFill(Color.WHITE);
		letterGuessBox.setAlignment(Pos.CENTER);
		letterGuessBox.setStyle("-fx-font-size:20");
		letterGuessBox.setPrefSize(250,50);

		// "Start Guess the Word!" button
		openingScreenButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				introSong.pause();

				clientConnection = new GuessClient(data->{
					Platform.runLater(()->{listItems.getItems().add(data.toString());
						int lastMessage = listItems.getItems().size();
						listItems.scrollTo(lastMessage);
						});
				}, Integer.parseInt(portBox.getText()), ipBox.getText());
				clientConnection.start();
				mainMenuSong.setVolume(0.25);
				mainMenuSong.setCycleCount(MediaPlayer.INDEFINITE);
				mainMenuSong.play();
				primaryStage.setScene(sceneMap.get("main screen"));
			}
		});

		//sets up the video game category and gets a word from the server
		gameButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Video Games");

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//gets the word from the server and the number of guesses
				guessesLeft.setText("You have " + (6 - clientConnection.clientInfo.getNumWrongGuesses()) + " guesses left!");
				wordText.setText(clientConnection.clientInfo.getWord().replace("", " ").trim());

				//removes everything from the pane & adds them back into the pane
				gameScreenPane = new Pane();
				gameScreenPane.getChildren().removeAll();
				VBox vBox = new VBox(wordText, letterGuessBox, guessButton);
				vBox.setSpacing(20);
				vBox.setAlignment(Pos.CENTER);
				gameScreenPane.getChildren().addAll(vBox, listItems, guessesLeft);

				//relocates the items in pane
				vBox.relocate(375,375);
				guessesLeft.relocate(50, 20);
				listItems.relocate(100,725);

				gameScreenPane.setBackground(new Background(new BackgroundImage(new Image("videoGames.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				primaryStage.setScene(new Scene(gameScreenPane, 1000, 900));
			}
		});

		//sets up the sports category and gets a word from the server
		sportsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Sports");

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//gets the word from the server and the number of guesses
				guessesLeft.setText("You have " + (6 - clientConnection.clientInfo.getNumWrongGuesses()) + " guesses left!");
				wordText.setText(clientConnection.clientInfo.getWord().replace("", " ").trim());

				//removes everything from the pane & adds them back into the pane
				sportsPane = new Pane();
				sportsPane.getChildren().removeAll();
				VBox vBox = new VBox(wordText, letterGuessBox, guessButton);
				vBox.setSpacing(20);
				vBox.setAlignment(Pos.CENTER);
				sportsPane.getChildren().addAll(vBox, listItems, guessesLeft);

				//relocates the items in pane
				vBox.relocate(350,550);
				guessesLeft.relocate(50, 20);
				listItems.relocate(100,725);

				sportsPane.setBackground(new Background(new BackgroundImage(new Image("sports.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				primaryStage.setScene(new Scene(sportsPane, 1000, 900));
			}
		});

		//sets up the food category and gets a word from the server
		foodButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("Foods");

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//gets the word from the server and the number of guesses
				wordText.setText(clientConnection.clientInfo.getWord().replace("", " ").trim());
				guessesLeft.setText("You have " + (6 - clientConnection.clientInfo.getNumWrongGuesses()) + " guesses left!");

				//removes everything from the pane & adds them back into the pane
				foodPane = new Pane();
				foodPane.getChildren().removeAll();
				VBox vBox = new VBox(wordText, letterGuessBox, guessButton);
				vBox.setSpacing(20);
				vBox.setAlignment(Pos.CENTER);
				foodPane.getChildren().addAll(vBox, listItems, guessesLeft);

				//relocates the items in pane
				vBox.relocate(360,300);
				guessesLeft.relocate(50, 20);
				listItems.relocate(100,725);

				foodPane.setBackground(new Background(new BackgroundImage(new Image("food.jpg", 1000, 900, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
				primaryStage.setScene(new Scene(foodPane, 1000, 900));
			}
		});

		//the Guess button which evaluates the character to see if it is a valid choice
		guessButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(letterGuessBox.getText().isEmpty()){
					letterGuessBox.setPromptText("Nothing is in here!");
				}
				else if(Character.isLetter(letterGuessBox.getText().charAt(0)) && letterGuessBox.getText().length() == 1){
					clientConnection.send(letterGuessBox.getText().toUpperCase());
					letterGuessBox.clear();
				}
				else{
					letterGuessBox.clear();
					listItems.getItems().add("Try Again! Character is not valid");
				}

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				wordText.setText(clientConnection.clientInfo.getWord().replace("", " ").trim());

				if(clientConnection.clientInfo.getNumWrongGuesses() != guessedWrong){
					guessesLeft.setText("You now have " + (6 - clientConnection.clientInfo.getNumWrongGuesses()) + " guesses");
				}

				if((6 - clientConnection.clientInfo.getNumWrongGuesses()) == 0 || clientConnection.clientInfo.getNumWordsGuessed() != currentWins){
					currentWins++;
					clientConnection.clientInfo.clearGuesses();

					//if-statements that check to see if you guessed the word correctly
					if(clientConnection.clientInfo.getCategories().contains("Video Games") && !gameButton.isDisable()){
						gameScreenPane.getChildren().removeAll();
						listItems.getItems().clear();
						gameButton.setDisable(true);
					}
					else if(clientConnection.clientInfo.getCategories().contains("Sports") && !sportsButton.isDisable()){
						sportsPane.getChildren().removeAll();
						listItems.getItems().clear();
						sportsButton.setDisable(true);
					}
					else if(clientConnection.clientInfo.getCategories().contains("Foods") && !foodButton.isDisable()){
						foodPane.getChildren().removeAll();
						listItems.getItems().clear();
						foodButton.setDisable(true);
					}
					primaryStage.setScene(sceneMap.get("main screen"));
				}
				//if the user wins sets up and shows the win screen
				if(sportsButton.isDisable() && foodButton.isDisable() && gameButton.isDisable()){
					//sets up the win screen when the player wins the game
					winPane = new Pane();
					winPane.setBackground(new Background(new BackgroundImage(new Image("winner.jpg", 608, 342, true,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
					winPane.getChildren().removeAll();
					HBox hBox = new HBox(playAgain, quitButton);
					hBox.setSpacing(100);
					winPane.getChildren().addAll(hBox);
					hBox.relocate(200,300);
					primaryStage.setScene(new Scene(winPane, 608,342));
					winSong.setVolume(0.25);
					winSong.setCycleCount(MediaPlayer.INDEFINITE);
					winSong.play();
				}
				//if the user loses sets up and shows the losing screen
				if(clientConnection.clientInfo.getWord().equals("game over")){
					losePane = new Pane();
					losePane.setBackground(new Background(new BackgroundImage(new Image("loser.png", 500, 400, true,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
					losePane.getChildren().removeAll();
					HBox hbox = new HBox(playAgain, quitButton);
					hbox.setSpacing(150);
					losePane.getChildren().addAll(hbox);
					hbox.relocate(100, 300);
					primaryStage.setScene(new Scene(losePane, 500,400));
				}
			}
		});

		quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clientConnection.send("no");
				Platform.exit();
				System.exit(0);
			}
		});

		playAgain.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gameButton.setDisable(false);
				foodButton.setDisable(false);
				sportsButton.setDisable(false);
				clearClient();
				listItems.getItems().clear();
				clientConnection.send("yes");
				currentWins = 0;
				guessedWrong = 0;
				primaryStage.setScene(sceneMap.get("main screen"));
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
		introSong.setVolume(0.25);
		introSong.setCycleCount(MediaPlayer.INDEFINITE);
		introSong.play();
	}
	private void clearClient(){
		clientConnection.clientInfo.setNumWordsGuessed(0);
		clientConnection.clientInfo.setNumWrongGuesses(0);
		clientConnection.clientInfo.getCategories().clear();
		clientConnection.clientInfo.setWord("");
		clientConnection.clientInfo.clearGuesses();
	}

}
