package ru.simplecloudstorage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.simplecloudstorage.client.ClientConnector;
import ru.simplecloudstorage.client.ClientSender;
import ru.simplecloudstorage.controllers.AuthDialog;
import ru.simplecloudstorage.controllers.MainWindow;
import ru.simplecloudstorage.controllers.NewFolderDialog;
import ru.simplecloudstorage.utils.ErrorDialog;
import ru.simplecloudstorage.utils.SceneName;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientApp extends Application {

    private static final int DEFAULT_PORT_VALUE = 8189;
    private static final String DEFAULT_HOST_VALUE = "localhost";
    private static final Logger logger = LogManager.getLogger(ClientApp.class);

    private static Scene scene;
    private static Stage primaryStage;
    private static FXMLLoader primaryLoader;
    private static Scene authScene;
    private static Stage authStage;
    private static String currentScene;
    private static String fromScene;
    private ClientConnector connector;
    private MainWindow mainWindow;
    private AuthDialog authController;
    private NewFolderDialog newFolderController;
    private ExecutorService mainWorkPool;
    private Stage newFolderDialogStage;
    public static int consolePort;
    public static String consoleHost;

    public static void main(String[] args) {
        if(args.length == 2) {
            consoleHost = args[0];
            consolePort = Integer.parseInt(args[1]);
        } else {
            consolePort = DEFAULT_PORT_VALUE;
            consoleHost = DEFAULT_HOST_VALUE;
        }

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        mainWorkPool = Executors.newSingleThreadExecutor();
        primaryStage = stage;
        scene = new Scene(loadFXML(SceneName.MAIN_WINDOW.getValue()));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Cloud Storage");
        primaryStage.setResizable(true);
        primaryStage.show();
        mainWindow = primaryLoader.getController();
        mainWindow.setApplication(this);
        newConnector();
        authDialogShow();
        newFolderDialogCreate();
        closeRequestCheck();
    }

    public void newConnector() {
        connector = new ClientConnector();
        connector.setApplication(this);
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
        connecting();
        authStage.show();
    }

    private void newFolderDialogCreate() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource(SceneName.NEW_FOLDER_DIALOG.getValue()));
        Pane dialogPanel = loader.load();
        Scene scene = new Scene(dialogPanel);
        newFolderDialogStage = new Stage();
        newFolderDialogStage.initOwner(primaryStage);
        newFolderDialogStage.initModality(Modality.WINDOW_MODAL);
        newFolderDialogStage.setResizable(false);
        newFolderDialogStage.setScene(scene);
        newFolderController = loader.getController();
        newFolderController.setApplication(this);
    }
    public void authDialogClose() {
        Platform.runLater(() -> {
            authStage.close();
            mainWindow.rightViewInit();
        });
    }

    private void connecting() {
        mainWorkPool.execute(() -> {
            connector.run();
        });
    }

    public static void authDialogSetRoot(String fxml, String from) {
        try {
            authScene.setRoot(loadFXML(fxml));
            currentScene = fxml;
            fromScene = from;
            authStage.sizeToScene();
        } catch (IOException e) {
            new ErrorDialog("???????????? ????????????????????", e.getMessage(), "???????????????????? ?????? ??????");
            logger.error(e.getMessage());
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
            new ErrorDialog("???????????? ????????????????????", e.getMessage(), "???????????????????? ?????? ??????");
            logger.error(e.getMessage());
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        primaryLoader = new FXMLLoader(ClientApp.class.getResource(fxml));
        return primaryLoader.load();
    }

    private void closeRequestCheck() {
        primaryStage.setOnCloseRequest( event -> {
            connector.connectorShutdown();
            mainWorkPool.shutdownNow();
        });
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

    public void mainWindowSetDownloader(ClientSender clientSender) {
        mainWindow.setDownloader(clientSender);

    }
    public NewFolderDialog getNewFolderDialog() {
        return newFolderController;
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getNewFolderDialogStage() {
        return newFolderDialogStage;
    }
}