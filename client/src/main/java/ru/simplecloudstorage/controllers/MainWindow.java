package ru.simplecloudstorage.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.simplecloudstorage.ClientApp;
import ru.simplecloudstorage.client.ClientDownloader;
import ru.simplecloudstorage.utils.SceneName;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    @FXML
    public MenuItem settingsMenu;
    @FXML
    public Button uploadButton;
    @FXML
    public ComboBox diskBox;
    @FXML
    private BorderPane mainPane;
    @FXML
    private TreeView leftView;
    @FXML
    private TreeView rightView;
    @FXML
    private HBox leftPane;
    @FXML
    private HBox rightPane;
    private ClientApp application;
    private ClientDownloader clientDownloader;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       diskBoxInit();
       rightViewInit();
    }

    public void rightViewInit() {
        Platform.runLater(() -> createRightViewTree(diskBox.getValue().toString()));
    }


    private void diskBoxInit() {
        diskBox.setItems(diskListCreate());
        diskBox.getSelectionModel().select(0);
    }

    private ObservableList diskListCreate() {
        File[] paths;
        ObservableList<String> disksList = FXCollections.observableArrayList();
        paths = File.listRoots();
        for (File path : paths) {
            disksList.add(path.toString().substring(0,2));
        }
        return disksList;
    }


    @FXML
    private void paneZoom(ZoomEvent zoomEvent) {
        leftPane.setPrefWidth((mainPane.getWidth()/2) - 40);
        rightPane.setPrefWidth((mainPane.getWidth()/2) - 40);
    }

    @FXML
    private void SettingsAction(ActionEvent actionEvent) {
        ClientApp.setRoot(SceneName.SETTINGS_WINDOW.getValue(), SceneName.MAIN_WINDOW.getValue());
    }


    @FXML
    private void closeMenuItemAction(ActionEvent actionEvent) {
    }

    @FXML
    private void uploadAction(ActionEvent actionEvent) {
    }

    public void setApplication(ClientApp clientApp) {
        this.application = clientApp;
    }

    public void setClientDownloader(ClientDownloader clientDownloader) {
        this.clientDownloader = clientDownloader;
    }

    public void setDownloader(ClientDownloader clientDownloader) {
        this.clientDownloader = clientDownloader;
    }

    private void createRightViewTree(String disk) {
        Path rootDisk = Paths.get(disk + "\\").toAbsolutePath().normalize();
        TreeItem<String> rootUserItem = new TreeItem<>(rootDisk.getFileName().toString());
        rightView.setShowRoot(false);
        fillFilesTree(rootDisk.toFile(), rootUserItem);
        rightView.setRoot(rootUserItem);
    }

    private void fillFilesTree(File target, TreeItem<String> item) {
        Image folder = new Image("folder.png");
        Image file = new Image("file.png");
        ImageView folderIcon = new ImageView(folder); //new ImageView(new Image(getClass().getResourceAsStream("folder.png")));
        ImageView fileIcon = new ImageView(file); //new ImageView(new Image(getClass().getResourceAsStream("file.png")));

        if (target.isDirectory() && target.canRead()) {
            TreeItem<String> treeItem = new TreeItem<>(target.getName());
            treeItem.setGraphic(folderIcon);
            item.getChildren().add(treeItem);
            for (File element : target.listFiles()) {
                fillFilesTree(element,treeItem);
            }
        } else {
            TreeItem<String> treeItem = new TreeItem<>(target.getName());
            treeItem.setGraphic(fileIcon);
            item.getChildren().add(treeItem);
        }
    }

}
