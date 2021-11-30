package ru.simplecloudstorage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.simplecloudstorage.client.ClientConnector;
import ru.simplecloudstorage.controllers.AuthDialog;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;

public class ClientApp extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static Scene authScene;
    private static Stage authStage;
    private static String currentScene;
    private static String fromScene;
    private ClientConnector connector;

    private AuthDialog authController;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML(SceneName.MAIN_WINDOW.getValue()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Cloud Storage");
        primaryStage.setResizable(true);
        primaryStage.show();
        connector = new ClientConnector();
        connector.setApplication(this);
        authDialogShow();
        closeRequestCheck();
    }


    private void authDialogShow() throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(ClientApp.class.getResource(SceneName.AUTH_DIALOG.getValue()));
        Pane authDialogPanel = authLoader.load();
        currentScene = SceneName.AUTH_DIALOG.getValue();
        authScene = new Scene(authDialogPanel);
        authStage = new Stage();
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setResizable(false);
        authStage.setScene(authScene);
        authController = authLoader.getController();
        authController.setClientApp(this);
        authController.setConnector(connector);
        authController.setAuthStage(authStage);
        connecting();
        authStage.show();
    }
    public void authDialogClose() {
        Platform.runLater(() -> {
            authStage.close();
        });
    }

    private void connecting() {
        new Thread(() -> {
            connector.run();
        }).start();
    }

    public static void authDialogSetRoot(String fxml, String from) {
        try {
            authScene.setRoot(loadFXML(fxml));
            currentScene = fxml;
            fromScene = from;
            authStage.sizeToScene();
        } catch (IOException e) {
            new ErrorDialog("Ошибка приложения", e.getMessage(), "Попробуйте еще раз");
        }
    }

    public ClientConnector getConnector() {
        return connector;
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
        primaryStage.setOnCloseRequest( event -> {
            connector.connectorShutdown();
        });
//        authStage.setOnHiding(windowEvent -> {
//            Platform.runLater(()-> {
//                primaryStage.close();
//            });
//        });
    }

    public static String getCurrentScene() {
        return currentScene;
    }

    public static String getFromScene() {
        return fromScene;
    }

    public AuthDialog getAuthDialog() {
        return authController;
    }
}