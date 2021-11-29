package ru.simplecloudstorage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class ClientApp extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static String currentScene;
    private static String fromScene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        currentScene = SceneName.AUTH_DIALOG.getValue();
        scene = new Scene(loadFXML(currentScene));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Cloud Storage");
        primaryStage.setResizable(false);
        primaryStage.show();
        closeRequestCheck();
    }

    public static void setRoot(String fxml, String from) {
        try {
            if (fxml.equals(SceneName.MAIN_WINDOW.getValue())) primaryStage.setResizable(true);
            else primaryStage.setResizable(false);
            scene.setRoot(loadFXML(fxml));
            currentScene = fxml;
            fromScene = from;
            primaryStage.sizeToScene();
        } catch (IOException e) {
            new ErrorDialog("Ошибка приложения", e.getMessage(), "Попробуйте еще раз");
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource(fxml));
        return fxmlLoader.load();
    }

    private void closeRequestCheck() {
        primaryStage.setOnCloseRequest( event ->
        {
            if (!currentScene.equals(SceneName.MAIN_WINDOW.getValue()) && !currentScene.equals(SceneName.AUTH_DIALOG.getValue())) {
                event.consume();
                setRoot(fromScene, currentScene);
            }
        });
    }

    public static String getCurrentScene() {
        return currentScene;
    }

    public static String getFromScene() {
        return fromScene;
    }
}