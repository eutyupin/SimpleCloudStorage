package ru.simplecloudstorage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.simplecloudstorage.utils.ErrorDialog;

import java.io.IOException;

public class ClientApp extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("authdialog"));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Cloud Storage");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void setRoot(String fxml) {
        try {
            if (fxml.equals("mainwindow")) primaryStage.setResizable(true);
            else primaryStage.setResizable(false);

            scene.setRoot(loadFXML(fxml));
            primaryStage.sizeToScene();
        } catch (IOException e) {
            new ErrorDialog("Ошибка приложения", e.getMessage(), "Попробуйте еще раз");
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}