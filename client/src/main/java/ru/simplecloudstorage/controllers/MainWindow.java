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
import javafx.scene.input.MouseEvent;
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

    private String serverPath, clientPath;
    @FXML
    public MenuItem settingsMenu;
    @FXML
    public Button uploadButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button newFolderButton;
    @FXML
    public Button downloadButton;
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
        excludeFoldersAdd();
        deleteButton.graphicProperty().setValue(new ImageView(new Image("delete.png")));
        newFolderButton.graphicProperty().setValue(new ImageView(new Image("add.png")));
        uploadButton.graphicProperty().setValue(new ImageView(new Image("p-c.png")));
        downloadButton.graphicProperty().setValue(new ImageView(new Image("c-p.png")));
    }

    public void rightViewInit() {
        Platform.runLater(() -> createRightViewTree(diskBox.getValue().toString()));
    }


    private void diskBoxInit() {
        diskBox.setItems(diskListCreate());
        diskBox.getSelectionModel().select(0);
    }

    private ObservableList diskListCreate() {
        ObservableList<String> disksList = FXCollections.observableArrayList();
        for (File path : File.listRoots()) {
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
        File rootDisk = new File(disk + "\\");
        TreeItem<String> rootPCItem = new TreeItem<>(rootDisk.getPath().toString());
        rightView.setShowRoot(false);
        fillFilesTree(rootDisk, rootPCItem);
        rightView.setRoot(rootPCItem);
    }

    private void fillFilesTree(File target, TreeItem<String> item) {

        if (target.isDirectory()) {
            String itemName;
            if (target.getName().length() == 0) itemName = diskBox.getValue().toString() + "\\";
            else itemName = target.getName();
            TreeItem<String> treeItem = new TreeItem<>(itemName);
            treeItem.setGraphic(new ImageView(new Image("folder.png")));
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
            treeItem.setGraphic(new ImageView(new Image("file.png")));
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

    public void setLeftView(TreeItem<String> tree) {
        leftView.setShowRoot(true);
        Platform.runLater(() -> leftView.setRoot(tree));
    }

    public void diskBoxAction(ActionEvent actionEvent) {
        rightViewInit();
    }

    public void diskBoxUpdate(MouseEvent mouseEvent) {
        diskBox.setItems(diskListCreate());
    }

    @FXML
    public void rightViewClicked(MouseEvent mouseEvent) {
    }

    @FXML
    public void leftViewClicked(MouseEvent mouseEvent) {
    }
}
