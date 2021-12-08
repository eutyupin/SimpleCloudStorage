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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    @FXML
    public MenuItem settingsMenu;
    @FXML
    public Button uploadButton;
    @FXML
    private ComboBox diskBox;
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
    private  List<String> excludeList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       diskBoxInit();
       rightViewInit();
       excludeFoldersAdd();
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
        File rootDisk = new File(disk + "/");
        TreeItem<String> rootUserItem = new TreeItem<>(rootDisk.getPath().toString());
        rightView.setShowRoot(false);
        fillFilesTree(rootDisk, rootUserItem);
        rightView.setRoot(rootUserItem);
    }

    private void fillFilesTree(File target, TreeItem<String> item) {
        ImageView folderIcon = new ImageView(new Image("folder.png"));
        ImageView fileIcon = new ImageView(new Image("file.png"));

        if (target.isDirectory() && target.canRead()) {
            TreeItem<String> treeItem = new TreeItem<>(target.getName());
            treeItem.setGraphic(folderIcon);
            item.getChildren().add(treeItem);
            for (File element : target.listFiles()) {
                String tempName = element.getPath().toString().substring(3);
                boolean canAdd = false;
                for (String s : excludeList) {
                    if(tempName.equals(s)) {
                        canAdd = false;
                        break;
                    } else canAdd = true;
                }
                if(canAdd) fillFilesTree(element,treeItem);
                else continue;
            }
        } else {
            TreeItem<String> treeItem = new TreeItem<>(target.getName());
            treeItem.setGraphic(fileIcon);
            item.getChildren().add(treeItem);
        }
    }

    private void excludeFoldersAdd() {
        excludeList = new ArrayList<>();
        excludeList.add("$Recycle.Bin");
        excludeList.add("$WinREAgent");
        excludeList.add("Documents and Settings");
        excludeList.add("Program Files");
        excludeList.add("Program Files (x86)");
        excludeList.add("ProgramData");
        excludeList.add("MSOCache");
        excludeList.add("System Volume Information");
        excludeList.add("Users");
        excludeList.add("Windows");
    }
}
