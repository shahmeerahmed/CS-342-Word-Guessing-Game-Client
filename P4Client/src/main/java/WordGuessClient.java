import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
	Pane mainScenePane;
	Button openingScreenButton;
	Button gameButton;
	Button sportsButton;
	Button foodButton;
	GuessClient clientConnection;
	HashMap<String, Scene> sceneMap = new HashMap<>();
	ListView<String> listItems2;

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
		portBox = new TextField();

		// prevent user from being able to enter more than 4 characters for port
		portBox.setTextFormatter(new TextFormatter<String>(change ->
				change.getControlNewText().length() <= 4 ? change : null));

		// textbox for entering port
		portText = new Text("Enter a port:");
		portText.setFont(Font.font ("Verdana", 20));
		portText.setStyle("-fx-font-weight: bold");
		portText.setFill(Color.WHITE);

		// textbox for entering ip address
		ipBox = new TextField();
		ipText = new Text("Enter an IP:");
		ipText.setFont(Font.font ("Verdana", 20));
		ipText.setStyle("-fx-font-weight: bold");
		ipText.setFill(Color.WHITE);

		openingScreenButton = new Button("Start Guess the Word!");
		gameButton = new Button("Games");
		sportsButton = new Button("Sports");
		foodButton = new Button("Food");

		//sets up the start screen
		Pane startPane = new Pane();
		startPane.setBackground(new Background(new BackgroundImage(new Image("randomLetters.png", 532, 720, false,true), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,  BackgroundSize.DEFAULT)));
		startPane.getChildren().addAll(ipText, ipBox, portText, portBox, openingScreenButton);
		portText.relocate(185, 200);
		portBox.relocate(165,230);
		ipText.relocate(190,300);
		ipBox.relocate(165, 330);
		openingScreenButton.relocate(170, 400);

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

				sceneMap.put("main screen", new Scene(mainScenePane,400,470));
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
		sceneMap.put("start screen", new Scene(startPane, 532, 720));
		primaryStage.setScene(sceneMap.get("start screen"));
		primaryStage.show();
	}

}
