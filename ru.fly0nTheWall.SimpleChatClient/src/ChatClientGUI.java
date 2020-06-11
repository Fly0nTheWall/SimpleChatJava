import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ChatClientGUI extends Application {
    public static ChatGUIListener guiListener;
    public static ObservableList<String> inputList;
    public static ObservableList<String> outputList;


    public ChatClientGUI() {
    }

    public ChatClientGUI(ChatGUIListener listener){
        guiListener = listener;
        launch("");
    }

    @Override
    public void start(Stage primaryStage) {
        ObservableList<String> inputMessagesList = FXCollections.observableList(new ArrayList<String>());
        inputList = inputMessagesList;
        ListView<String> inputMessagesView = new ListView<>(inputMessagesList);

        ObservableList<String> outputMessagesList = FXCollections.observableList(new ArrayList<String>());
        outputList = outputMessagesList;
        ListView<String> outputMessagesView =new ListView<>(outputMessagesList);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter your message here...");
        inputField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String message = inputField.getText();
                inputField.clear();
                outputMessagesList.add(message);
                inputMessagesList.add("");
                guiListener.getMessageFromGUI(message);
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(inputMessagesView);
        borderPane.setRight(outputMessagesView);
        borderPane.setCenter(inputField);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
